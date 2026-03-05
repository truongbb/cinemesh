package com.cinemesh.paymentservice.statics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentNotificationError {

    SUCCESS("00", "Confirm Success"),
    ORDER_NOT_FOUND("01", "Order not found"),
    ORDER_ALREADY_CONFIRMED("02", "Order already confirmed"),
    INVALID_AMOUNT("04", "Invalid amount"),
    INVALID_CHECKSUM("97", "Invalid Checksum"),
    UNKNOWN_ERROR("99", "Unknown error"),

    ;

    private final String code;
    private final String message;

}
