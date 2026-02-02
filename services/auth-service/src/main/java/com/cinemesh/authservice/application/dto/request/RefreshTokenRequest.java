package com.cinemesh.authservice.application.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    @NotBlank
    String refreshToken;

}