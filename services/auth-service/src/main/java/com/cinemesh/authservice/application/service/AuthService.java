package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.application.dto.request.RegisterRequest;
import com.cinemesh.authservice.application.dto.response.RegisterResponse;
import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrashtructure.persistence.entity.RoleEntity;
import com.cinemesh.authservice.infrashtructure.persistence.repository.RoleRepository;
import com.cinemesh.authservice.infrashtructure.persistence.repository.UserRepository;
import com.cinemesh.authservice.statics.UserStatus;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    ObjectMapper objectMapper;
    UserSaveService userSaveService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnprocessableEntityException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = buildNewUser(request);
        userSaveService.saveUser(user);

        sendActivationEmail(user);

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .status(user.getStatus())
                .build();
    }

    private User buildNewUser(RegisterRequest request) {
        RoleEntity customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.ROLE_NOT_FOUND));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.INACTIVE);

        RoleDto roleDto = objectMapper.convertValue(customerRole, RoleDto.class);
        user.addRoles(List.of(roleDto));

        return user;
    }

    @Async
    public void sendActivationEmail(User user) {
        // TODO - send activation email
    }

}
