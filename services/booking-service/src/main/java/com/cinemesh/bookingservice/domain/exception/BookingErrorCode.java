package com.cinemesh.bookingservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum BookingErrorCode implements BaseErrorCode {
    TICKET_NOT_FOUND("005001"),
    SEATS_ALREADY_TAKEN("005002"),
    SEAT_NOT_FOUND_IN_SHOWTIME("005003"),
    CHECKOUT_PROCESSING_FAILED("005004");

    private final String code;

    BookingErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
