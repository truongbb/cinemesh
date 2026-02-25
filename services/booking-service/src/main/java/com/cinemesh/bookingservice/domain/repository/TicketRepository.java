package com.cinemesh.bookingservice.domain.repository;

import com.cinemesh.bookingservice.domain.model.Ticket;

import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    Optional<Ticket> findById(UUID id);

}
