package com.cinemesh.authservice.application.dto.response;

import com.cinemesh.authservice.statics.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegisterResponse {
    private UUID id;
    private String email;
    private UserStatus status;
}
