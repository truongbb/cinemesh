package com.cinemesh.bookingservice.application.service;

import com.cinemesh.bookingservice.application.dto.response.ShowtimeSeatResponse;
import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.feign.TheaterFeignClient;
import com.cinemesh.bookingservice.infrastructure.feign.response.ShowtimeResponse;
import com.cinemesh.bookingservice.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import com.cinemesh.bookingservice.infrastructure.persistence.adapter.TicketPersistenceAdapter;
import com.cinemesh.bookingservice.statics.SeatBookingStatus;
import com.cinemesh.bookingservice.statics.TicketStatus;
import com.cinemesh.common.statics.SeatType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingService {

    TheaterFeignClient theaterFeignClient;
    OrderPersistenceAdapter orderPersistenceAdapter;
    TicketPersistenceAdapter ticketPersistenceAdapter;


    public ShowtimeSeatResponse getSeatsOfShowtime(@NotNull UUID showtimeId) {
        ShowtimeResponse showtime = theaterFeignClient.getShowtimeDetail(showtimeId);

        // get ticket from DB to get seat status
        Map<UUID, TicketStatus> tickets = ticketPersistenceAdapter.findByShowtimeIdAndStatusIn(showtimeId, List.of(TicketStatus.BOOKED, TicketStatus.RESERVED))
                .stream().collect(Collectors.toMap(Ticket::getSeatId, Ticket::getStatus));

        // TODO - get seat status from redis
        // Set<UUID> redisLockedSeatIds = redisService.getLockedSeatsForShowtime(showtimeId);

        List<ShowtimeSeatResponse.ShowtimeSeatResponseDetail> seats = showtime.getRoom().getSeats()
                .stream()
                .map(seatResponse -> {
                    TicketStatus ticketStatus = tickets.get(seatResponse.getId());
                    SeatBookingStatus status = SeatBookingStatus.AVAILABLE;
                    if (ticketStatus != null) {
                        status = ticketStatus.equals(TicketStatus.RESERVED) ? SeatBookingStatus.LOCKED : SeatBookingStatus.SOLD;
                    }
                    // Check Redis Status (if DB didn't already mark it as sold/reserved)
                    // else if (redisLockedSeatIds.contains(currentSeatId)) {
                    //     status = SeatBookingStatus.LOCKED;
                    // }
                    return ShowtimeSeatResponse.ShowtimeSeatResponseDetail.builder()
                            .id(seatResponse.getId())
                            .type(seatResponse.getType())
                            .price(calculateSeatPrice(showtime.getBasePrice(), seatResponse.getType()))
                            .rowCode(seatResponse.getRowCode())
                            .columnNumber(seatResponse.getColumnNumber())
                            .status(status)
                            .build();
                })
                .toList();

        return new ShowtimeSeatResponse(seats);
    }

    // Simple helper method to keep your map() clean
    private BigDecimal calculateSeatPrice(BigDecimal basePrice, SeatType type) {
        // TODO - cần có cơ chế set price khác nhau giữa các loại phim, loại ghế và giờ chiếu
        // Example: VIP seats cost 50,000 more
        if (SeatType.VIP.equals(type)) {
            return basePrice.add(new BigDecimal("50000"));
        } else if (SeatType.COUPLE.equals(type)) {
            return basePrice.multiply(new BigDecimal("2")); // Couple seats usually double
        }
        return basePrice;
    }


}
