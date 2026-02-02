package com.cinemesh.authservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum AuthErrorCode implements BaseErrorCode {
    ROLE_NOT_FOUND("001001"),
    EMAIL_ALREADY_EXISTS("001002"),
    USER_EMAIL_NOT_FOUND("001003"),
    INVALID_REFRESH_TOKEN("001004"),

    ;

    private final String code;

    AuthErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
