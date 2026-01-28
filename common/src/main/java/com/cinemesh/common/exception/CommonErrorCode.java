package com.cinemesh.common.exception;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements BaseErrorCode {
    INTERNAL_SERVER_ERROR("500"),
    INVALID_REQUEST("400"),
    RESOURCE_NOT_FOUND("404"),
    UNAUTHORIZED("401"),
    OPTIMISTIC_LOCK_UNPROCESSABLE("422"),
    VALIDATION_ERROR("422");

    private final String code;

    CommonErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
