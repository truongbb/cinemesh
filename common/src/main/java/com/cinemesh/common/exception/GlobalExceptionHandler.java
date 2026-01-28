package com.cinemesh.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final MessageSource messageSource;

    // Helper lấy message từ file properties
    private String getMessage(String code, String defaultMessage) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return defaultMessage;
        }
    }

    // 1. XỬ LÝ MỌI LỖI NGHIỆP VỤ (CinemeshException)
    @ExceptionHandler(CinemeshException.class)
    public ResponseEntity<ErrorResponse> handleCinemeshException(CinemeshException ex) {
        List<BaseErrorCode> errorCodes = ex.getErrorCodes();

        // Map từ Enum Error Code sang ErrorDetail
        List<ErrorResponse.ErrorDetail> details = errorCodes.stream()
                .map(code -> {
                    // Logic lấy message:
                    // - Nếu là lỗi đơn VÀ message trong exception khác với code -> Dùng message đó (Custom message)
                    // - Ngược lại -> Tra từ điển (Properties)
                    String msg = (errorCodes.size() == 1 && !ex.getMessage().equals(code.getCode()) && !ex.getMessage().equals("Multiple errors occurred"))
                            ? ex.getMessage()
                            : getMessage(code.getCode(), code.getCode());

                    return ErrorResponse.ErrorDetail.builder()
                            .code(code.getCode())
                            .message(msg)
                            .build();
                })
                .toList();

        return new ResponseEntity<>(
                ErrorResponse.builder().errors(details).build(),
                ex.getStatus()
        );
    }

    // 2. XỬ LÝ LỖI VALIDATE INPUT (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ErrorDetail> details = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();

            details.add(ErrorResponse.ErrorDetail.builder()
                    .code(CommonErrorCode.VALIDATION_ERROR.getCode())
                    .field(fieldName)
                    .message(errorMessage)
                    .build());
        });

        return new ResponseEntity<>(
                ErrorResponse.builder().errors(details).build(),
                HttpStatus.BAD_REQUEST
        );
    }

    // 3. XỬ LÝ LỖI SERVER (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        log.error("Unexpected error", ex);

        ErrorResponse.ErrorDetail detail = ErrorResponse.ErrorDetail.builder()
                .code(CommonErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message("Internal Server Error")
                .build();

        return new ResponseEntity<>(
                ErrorResponse.builder().errors(List.of(detail)).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
