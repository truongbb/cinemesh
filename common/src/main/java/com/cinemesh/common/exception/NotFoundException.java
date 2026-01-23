package com.cinemesh.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundException extends CinemeshException {

    public NotFoundException(BaseErrorCode errorCode) {
        super(errorCode, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(BaseErrorCode errorCode, String customMessage) {
        super(errorCode, HttpStatus.NOT_FOUND, customMessage);
    }

    public NotFoundException(List<BaseErrorCode> errorCodes) {
        super(errorCodes, HttpStatus.NOT_FOUND);
    }
}
