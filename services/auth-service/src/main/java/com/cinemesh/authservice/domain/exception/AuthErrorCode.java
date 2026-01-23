package com.cinemesh.authservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum AuthErrorCode implements BaseErrorCode {
    ROLE_NOT_FOUND("001001")

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
