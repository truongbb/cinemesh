package com.cinemesh.notificationservice.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum NotificationErrorCode implements BaseErrorCode {
    SEND_ACTIVATION_EMAIL_FAILED("002001"),

    ;

    private final String code;

    NotificationErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
