package com.cinemesh.authservice.presenration.rest;

import com.cinemesh.authservice.application.dto.request.LoginRequest;
import com.cinemesh.authservice.application.dto.request.RefreshTokenRequest;
import com.cinemesh.authservice.application.dto.request.RegisterRequest;
import com.cinemesh.authservice.application.dto.response.AuthenticationTokenResponse;
import com.cinemesh.authservice.application.dto.response.RegisterResponse;
import com.cinemesh.authservice.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authentications")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthenticationTokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public void logout() {
        authService.logout();
    }

    @PostMapping("/refresh")
    public AuthenticationTokenResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refresh(request);
    }


}
