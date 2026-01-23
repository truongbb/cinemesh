package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.application.dto.request.RegisterRequest;
import com.cinemesh.authservice.application.dto.response.RegisterResponse;
import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrashtructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrashtructure.persistence.repository.UserRepository;
import com.cinemesh.authservice.statics.UserStatus;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnprocessableEntityException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.INACTIVE);

        RoleDto roleDto = new RoleDto(null, RoleName.ROLE_CUSTOMER);
        user.setRoles(List.of(roleDto));

        UserEntity userEntity = objectMapper.convertValue(user, UserEntity.class);
        userRepository.save(userEntity);

        sendActivationEmail(user);

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    @Async
    public void sendActivationEmail(User user) {
        // TODO - send activation email
    }

}
