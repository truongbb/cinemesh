package com.cinemesh.authservice.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationTokenResponse {
    private String accessToken;
    private String refreshToken;
}
