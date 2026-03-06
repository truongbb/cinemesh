package com.cinemesh.bookingservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum BookingErrorCode implements BaseErrorCode {
    TICKET_NOT_FOUND("005001"),
    SEATS_ALREADY_TAKEN("005002"),
    SEAT_NOT_FOUND_IN_SHOWTIME("005003"),
    CHECKOUT_PROCESSING_FAILED("005004"),
    ORDER_NOT_FOUND("005005"),
    ORDER_STATUS_UPDATE_FAILED("005006"),
    PAYMENT_AMOUNT_NOT_ENOUGH("005007"),
    INVALID_ORDER_STATUS_TO_UPDATE_PAYMENT_RESULT("005008"),
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
