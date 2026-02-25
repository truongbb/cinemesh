package com.cinemesh.bookingservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum BookingErrorCode implements BaseErrorCode {
    TICKET_NOT_FOUND("005001")

    ;

    private final String code;

    BookingErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
