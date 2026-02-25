package com.cinemesh.bookingservice.domain.repository;

import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.statics.TicketStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {

    Optional<Ticket> findById(UUID id);

    List<Ticket> findByShowtimeIdAndStatusIn(UUID showtimeId, List<TicketStatus> statuses);

}
