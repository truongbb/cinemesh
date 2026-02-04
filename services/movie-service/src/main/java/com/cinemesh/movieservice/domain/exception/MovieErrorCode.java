package com.cinemesh.movieservice.domain.exception;

import com.cinemesh.common.exception.BaseErrorCode;
import lombok.Getter;

@Getter
public enum MovieErrorCode implements BaseErrorCode {
    GENRE_MUST_BE_EXISTED_BEFORE_CREATE_MOVIE("003001"),

    ;

    private final String code;

    MovieErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
