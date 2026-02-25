package com.cinemesh.theaterservice.domain.model;

import com.cinemesh.common.domain.BaseLocalEntity;
import com.cinemesh.common.domain.LocalEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.statics.SeatType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Seat extends BaseLocalEntity<Room, UUID> implements LocalEntity<Room, UUID> {

    private String rowCode;
    private Integer columnNumber;
    private SeatType type;

    public Seat() {
    }

    public Seat(Room room, SeatDto dto) {
        this.aggRoot = room;
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.rowCode = dto.getRowCode();
        this.columnNumber = dto.getColumnNumber();
        this.type = dto.getType();
        create();
    }

    public void update(SeatDto dto) {
        setType(dto.getType());
        setRowCode(dto.getRowCode());
        setColumnNumber(dto.getColumnNumber());
        modify();
    }

    private void setRowCode(String rowCode) {
        if (ObjectUtils.equals(this.rowCode, rowCode)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("rowCode", this.rowCode, rowCode)));
        this.rowCode = rowCode;
        modify();
    }

    private void setColumnNumber(Integer columnNumber) {
        if (ObjectUtils.equals(this.columnNumber, columnNumber)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("columnNumber", this.columnNumber, columnNumber)));
        this.columnNumber = columnNumber;
        modify();
    }

    private void setType(SeatType type) {
        if (ObjectUtils.equals(this.type, type)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("type", this.type, type)));
        this.type = type;
        modify();
    }

}
