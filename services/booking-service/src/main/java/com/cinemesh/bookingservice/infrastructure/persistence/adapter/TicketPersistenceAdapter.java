package com.cinemesh.bookingservice.infrastructure.persistence.adapter;

import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.persistence.repository.TicketRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketPersistenceAdapter implements com.cinemesh.bookingservice.domain.repository.TicketRepository {

    TicketRepository ticketRepository;

    @Override
    public Optional<Ticket> findById(UUID id) {
        return Optional.empty();
    }

}
