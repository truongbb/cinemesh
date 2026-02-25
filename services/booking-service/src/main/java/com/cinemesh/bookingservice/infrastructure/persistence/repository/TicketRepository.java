package com.cinemesh.bookingservice.infrastructure.persistence.repository;

import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.persistence.entity.TicketEntity;
import com.cinemesh.bookingservice.statics.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID>, JpaSpecificationExecutor<TicketEntity> {

    List<TicketEntity> findByShowtimeIdAndStatusIn(UUID showtimeId, List<TicketStatus> statuses);

}
