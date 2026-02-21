package com.cinemesh.theaterservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum TheaterErrorCode implements BaseErrorCode {
    SEAT_NOT_FOUND("004001"),
    ROOM_EXISTED("004002"),
    ROOM_NOT_FOUND("004003"),
    ROOM_STATUS_CANNOT_BACKWARD_TO_CREATED("004004"),
    SHOWTIME_START_TIME_MUST_GREATER_THAN_END_TIME("004005"),
    MOVIE_NOT_FOUND("004006"),
    ROOM_OCCUPIED("004007"),
    MOVIE_IS_NOT_PUBLISHED_TO_SHOW("004008"),
    SHOWTIME_NOT_FOUND("004009"),
    INVALID_SHOWTIME_STATUS_PROCESS("004010")

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
