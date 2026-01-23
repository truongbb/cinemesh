package com.cinemesh.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

@Getter
public class CinemeshException extends RuntimeException {
    private final List<BaseErrorCode> errorCodes;
    private final HttpStatus status;

    // Case 1: Lỗi đơn (Single Error) -> Thường dùng nhất
    public CinemeshException(BaseErrorCode errorCode, HttpStatus status) {
        super(errorCode.getCode());
        this.errorCodes = Collections.singletonList(errorCode);
        this.status = status;
    }

    // Case 2: Lỗi đơn + Custom Message (Override message mặc định)
    public CinemeshException(BaseErrorCode errorCode, HttpStatus status, String customMessage) {
        super(customMessage);
        this.errorCodes = Collections.singletonList(errorCode);
        this.status = status;
    }

    // Case 3: Đa lỗi (Multiple Errors)
    public CinemeshException(List<BaseErrorCode> errorCodes, HttpStatus status) {
        super("Multiple errors occurred");
        this.errorCodes = errorCodes;
        this.status = status;
    }
}
