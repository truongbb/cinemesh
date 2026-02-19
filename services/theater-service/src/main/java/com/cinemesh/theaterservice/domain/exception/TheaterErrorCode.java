package com.cinemesh.theaterservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum TheaterErrorCode implements BaseErrorCode {
    SEAT_NOT_FOUND("004001"),
    ROOM_EXISTED("004002"),
    ROOM_NOT_FOUND("004003"),
    ROOM_STATUS_CANNOT_BACKWARD_TO_CREATED("004004"),

    ;

    private final String code;

    TheaterErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
