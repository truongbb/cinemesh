package com.cinemesh.bookingservice.application.scheduler;

import com.cinemesh.bookingservice.application.dto.TicketDto;
import com.cinemesh.bookingservice.domain.model.Order;
import com.cinemesh.bookingservice.infrastructure.persistence.adapter.OrderPersistenceAdapter;
import com.cinemesh.bookingservice.infrastructure.persistence.mapper.TickerMapper;
import com.cinemesh.bookingservice.statics.OrderStatus;
import com.cinemesh.bookingservice.statics.TicketStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExpiredOrderSweeper {

    final TickerMapper tickerMapper;
    final OrderPersistenceAdapter orderPersistenceAdapter;

    @Value("${application.booking.seat-locking-ttl-in-sec}")
    int seatLockingTtl;

    /**
     * Runs every 30 seconds.
     * The fixedDelay ensures a pod waits 30 seconds AFTER its previous run finishes
     * before starting again, preventing overlapping threads on the same pod.
     */
    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    @Transactional
    public void sweepExpiredOrders() {
        List<Order> expiredOrders = orderPersistenceAdapter.findExpiredOrdersForProcessing(seatLockingTtl);
        if (expiredOrders.isEmpty()) {
            return; // Nothing to process
        }

        log.info("Sweeper Job found {} expired orders. Cancelling...", expiredOrders.size());

        expiredOrders.forEach(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            List<TicketDto> ticketDtos = order.getTickets()
                    .stream()
                    .map(tickerMapper::convertDomainToDto)
                    .collect(Collectors.toList());

            ticketDtos.forEach(ticketDto -> ticketDto.setStatus(TicketStatus.CANCELLED));
            order.updateTickets(ticketDtos);
        });
        orderPersistenceAdapter.saveAllOrders(expiredOrders);

        log.info("Successfully cancelled {} expired orders.", expiredOrders.size());
    }

}
