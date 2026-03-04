package com.cinemesh.paymentservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {
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
