package com.cinemesh.bookingservice.infrastructure.persistence.adapter;

import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.persistence.mapper.TickerMapper;
import com.cinemesh.bookingservice.infrastructure.persistence.repository.TicketRepository;
import com.cinemesh.bookingservice.statics.TicketStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketPersistenceAdapter implements com.cinemesh.bookingservice.domain.repository.TicketRepository {

    TickerMapper tickerMapper;
    TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> findById(UUID id) {
        return ticketRepository.findById(id)
                .map(tickerMapper::convertEntityToDomain);
    }

    @Override
    public List<Ticket> findByShowtimeIdAndStatusIn(UUID showtimeId, List<TicketStatus> statuses) {
        return ticketRepository.findByShowtimeIdAndStatusIn(showtimeId, statuses)
                .stream()
                .map(tickerMapper::convertEntityToDomain)
                .toList();
    }

}
