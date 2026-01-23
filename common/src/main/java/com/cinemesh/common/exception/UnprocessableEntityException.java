package com.cinemesh.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class UnprocessableEntityException extends CinemeshException {

    public UnprocessableEntityException(BaseErrorCode errorCode) {
        super(errorCode, HttpStatus.UNPROCESSABLE_CONTENT);
    }

    public UnprocessableEntityException(BaseErrorCode errorCode, String customMessage) {
        super(errorCode, HttpStatus.UNPROCESSABLE_CONTENT, customMessage);
    }

    public UnprocessableEntityException(List<BaseErrorCode> errorCodes) {
        super(errorCodes, HttpStatus.UNPROCESSABLE_CONTENT);
    }

}
