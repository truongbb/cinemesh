package com.cinemesh.paymentservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {
    INVALID_ORDER_STATUS_FOR_PAYMENT("006001"),
    PAYMENT_NOT_FOUND("006002"),
    ;

    private final String code;

    PaymentErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
