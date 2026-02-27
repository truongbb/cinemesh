package com.cinemesh.bookingservice.infrastructure.persistence.mapper;

import com.cinemesh.bookingservice.application.dto.TicketDto;
import com.cinemesh.bookingservice.domain.model.Ticket;
import com.cinemesh.bookingservice.infrastructure.persistence.entity.TicketEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TickerMapper {

    ObjectMapper objectMapper;

    public Ticket convertEntityToDomain(TicketEntity ticketEntity) {
        return objectMapper.convertValue(ticketEntity, Ticket.class);
    }

    public TicketDto convertDomainToDto(Ticket ticket) {
        return objectMapper.convertValue(ticket, TicketDto.class);
    }

}
