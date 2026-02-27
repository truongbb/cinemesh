package com.cinemesh.bookingservice.application.service;

import com.cinemesh.bookingservice.application.dto.OrderDto;
import com.cinemesh.bookingservice.application.dto.TicketDto;
import com.cinemesh.bookingservice.application.dto.request.BookingCreationRequest;
import com.cinemesh.bookingservice.application.dto.response.OrderResponse;
import com.cinemesh.bookingservice.application.dto.response.ShowtimeSeatResponse;
import com.cinemesh.bookingservice.domain.exception.BookingErrorCode;
import com.cinemesh.bookingservice.domain.model.Order;
import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.feign.TheaterFeignClient;
import com.cinemesh.bookingservice.infrastructure.feign.UserFeignClient;
import com.cinemesh.bookingservice.infrastructure.feign.response.SeatResponse;
import com.cinemesh.bookingservice.infrastructure.feign.response.ShowtimeResponse;
import com.cinemesh.bookingservice.infrastructure.feign.response.UserResponse;
import com.cinemesh.bookingservice.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import com.cinemesh.bookingservice.infrastructure.persistence.adapter.TicketPersistenceAdapter;
import com.cinemesh.bookingservice.infrastructure.persistence.mapper.OrderMapper;
import com.cinemesh.bookingservice.statics.OrderPaymentStatus;
import com.cinemesh.bookingservice.statics.OrderStatus;
import com.cinemesh.bookingservice.statics.SeatBookingStatus;
import com.cinemesh.bookingservice.statics.TicketStatus;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.security.SecurityUtils;
import com.cinemesh.common.statics.SeatType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    OrderMapper orderMapper;
    UserFeignClient userFeignClient;
    RedisService redisService;
    TheaterFeignClient theaterFeignClient;
    OrderPersistenceAdapter orderPersistenceAdapter;
    TicketPersistenceAdapter ticketPersistenceAdapter;


    public ShowtimeSeatResponse getSeatsOfShowtime(@NotNull UUID showtimeId) {
        ShowtimeResponse showtime = theaterFeignClient.getShowtimeDetail(showtimeId);

        // get ticket from DB to get seat status
        Map<UUID, TicketStatus> tickets = ticketPersistenceAdapter.findByShowtimeIdAndStatusIn(showtimeId, List.of(TicketStatus.BOOKED, TicketStatus.RESERVED))
                .stream().collect(Collectors.toMap(Ticket::getSeatId, Ticket::getStatus));

        List<UUID> seatIds = showtime.getRoom().getSeats().stream().map(SeatResponse::getId).toList();
        Set<UUID> redisLockedSeatIds = redisService.getLockedSeatIds(showtimeId, seatIds);

        List<ShowtimeSeatResponse.ShowtimeSeatResponseDetail> seats = showtime.getRoom().getSeats()
                .stream()
                .map(seatResponse -> {
                    TicketStatus ticketStatus = tickets.get(seatResponse.getId());
                    SeatBookingStatus status = SeatBookingStatus.AVAILABLE;
                    if (ticketStatus != null) {
                        status = ticketStatus.equals(TicketStatus.RESERVED) ? SeatBookingStatus.LOCKED : SeatBookingStatus.SOLD;
                    } else if (redisLockedSeatIds.contains(seatResponse.getId())) {
                        status = SeatBookingStatus.LOCKED;
                    }
                    return ShowtimeSeatResponse.ShowtimeSeatResponseDetail.builder()
                            .id(seatResponse.getId())
                            .type(seatResponse.getType())
                            .price(calculateSeatPrice(showtime.getBasePrice(), seatResponse.getType()))
                            .rowCode(seatResponse.getRowCode())
                            .columnNumber(seatResponse.getColumnNumber())
                            .status(status)
                            .build();
                })
                .sorted(Comparator.comparing(ShowtimeSeatResponse.ShowtimeSeatResponseDetail::getRowCode))
                .toList();

        return new ShowtimeSeatResponse(seats);
    }

    // Simple helper method to keep your map() clean
    private BigDecimal calculateSeatPrice(BigDecimal basePrice, SeatType type) {
        // TODO - cần có cơ chế set price khác nhau giữa các loại phim, loại ghế và giờ chiếu, tạm thời thế này đã
        // Example: VIP seats cost 50k more
        if (SeatType.VIP.equals(type)) {
            return basePrice.add(new BigDecimal("50000"));
        } else if (SeatType.COUPLE.equals(type)) {
            return basePrice.multiply(new BigDecimal("2")); // Couple seats usually double
        }
        return basePrice;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createBooking(@Valid BookingCreationRequest request) {
        UUID showtimeId = request.getShowtimeId();
        List<UUID> requestedSeatIds = request.getSeatIds();
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserResponse user = userFeignClient.getUserByEmail(email);
        UUID userId = user.getId();

        // ==========================================
        // STEP 1: The Gatekeeper (Redis Atomic Lock)
        // ==========================================
        boolean isLocked = redisService.lockSeats(showtimeId, requestedSeatIds, userId);
        if (!isLocked) {
            log.warn("User {} attempted to book taken seats for showtime {}", userId, showtimeId);
            throw new UnprocessableEntityException(BookingErrorCode.SEATS_ALREADY_TAKEN);
        }

        // ==========================================
        // STEP 2: The Business Logic (Database)
        // ==========================================
        try {
            // 2a. Fetch layout to calculate REAL prices securely on the backend
            ShowtimeResponse showtime = theaterFeignClient.getShowtimeDetail(showtimeId);
            UUID orderId = UUID.randomUUID();

            List<TicketDto> tickets = new ArrayList<>();
            for (UUID seatId : requestedSeatIds) {
                SeatResponse seatDetail = findSeatDetail(showtime, seatId);
                TicketDto ticket = TicketDto.builder()
                        .seatId(seatId)
                        .showtimeId(showtimeId)
                        .price(calculateSeatPrice(showtime.getBasePrice(), seatDetail.getType()))
                        .status(TicketStatus.RESERVED)
                        .build();
                tickets.add(ticket);
            }
            BigDecimal totalPrice = calculateTotalPrice(tickets);

            // 2b. Create Order (Aggregate Root)
            OrderDto orderDto = OrderDto.builder()
                    .id(orderId)
                    .userId(userId)
                    .totalAmount(totalPrice)
                    .tickets(tickets)
                    .status(OrderStatus.PENDING)
                    .paymentStatus(OrderPaymentStatus.UNPAID)
                    .build();

            Order order = new Order(orderDto);
            orderPersistenceAdapter.saveOrder(order);

            return orderMapper.convertFromDomainToResponse(order);

        } catch (Exception e) {
            // ==========================================
            // STEP 3: The Safety Net (Rollback Redis)
            // ==========================================
            log.error("Database failure during checkout for user {}. Rolling back Redis locks.", userId, e);
            redisService.unlockSeats(showtimeId, requestedSeatIds);

            // Rethrow so the `@Transactional` rolls back any partial DB inserts,
            // and the GlobalExceptionHandler returns a 500 to the user.
            throw new UnprocessableEntityException(BookingErrorCode.CHECKOUT_PROCESSING_FAILED);
        }
    }

    private BigDecimal calculateTotalPrice(List<TicketDto> tickets) {
        BigDecimal total = BigDecimal.ZERO;
        for (TicketDto ticket : tickets) {
            total = total.add(ticket.getPrice());
        }
        return total;
    }

    private SeatResponse findSeatDetail(ShowtimeResponse showtime, UUID seatId) {
        return showtime.getRoom().getSeats().stream()
                .filter(seat -> seat.getId().equals(seatId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(BookingErrorCode.SEAT_NOT_FOUND_IN_SHOWTIME));
    }

}
