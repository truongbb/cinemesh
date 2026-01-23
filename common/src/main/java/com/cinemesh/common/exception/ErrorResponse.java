package com.cinemesh.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private List<ErrorDetail> errors;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL) // Field nào null thì ẩn đi (ví dụ field)
    public static class ErrorDetail {
        private String code;    // Mã lỗi (AUTH_001)
        private String message; // User not found
        private String field;   // Dùng cho validation (email, password)
    }

}
