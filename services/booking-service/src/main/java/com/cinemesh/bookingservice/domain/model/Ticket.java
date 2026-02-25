package com.cinemesh.bookingservice.domain.model;

import com.cinemesh.bookingservice.application.dto.TicketDto;
import com.cinemesh.bookingservice.statics.TicketStatus;
import com.cinemesh.common.domain.BaseLocalEntity;
import com.cinemesh.common.domain.LocalEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Ticket extends BaseLocalEntity<Order, UUID> implements LocalEntity<Order, UUID> {

    private UUID showtimeId;
    private UUID seatId;
    private BigDecimal price;
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(Order order, TicketDto dto) {
        this.aggRoot = order;
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.showtimeId = dto.getShowtimeId();
        this.seatId = dto.getSeatId();
        this.price = dto.getPrice();
        this.status = dto.getStatus();
        create();
    }

    public void update(TicketDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        setShowtimeId(dto.getShowtimeId());
        setSeatId(dto.getSeatId());
        setPrice(dto.getPrice());
        setStatus(dto.getStatus());
        modify();
    }

    private void setShowtimeId(UUID showtimeId) {
        if (ObjectUtils.equals(this.showtimeId, showtimeId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("showtimeId", this.showtimeId, showtimeId)));
        this.showtimeId = showtimeId;
        modify();
    }

    private void setSeatId(UUID seatId) {
        if (ObjectUtils.equals(this.seatId, seatId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("seatId", this.seatId, seatId)));
        this.seatId = seatId;
        modify();
    }

    private void setPrice(BigDecimal price) {
        if (ObjectUtils.equals(this.price, price)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("price", this.price, price)));
        this.price = price;
        modify();
    }

    private void setStatus(TicketStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }


}
