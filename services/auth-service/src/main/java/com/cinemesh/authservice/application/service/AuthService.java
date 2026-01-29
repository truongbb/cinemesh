package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.application.dto.request.LoginRequest;
import com.cinemesh.authservice.application.dto.request.RegisterRequest;
import com.cinemesh.authservice.application.dto.response.LoginResponse;
import com.cinemesh.authservice.application.dto.response.RegisterResponse;
import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrashtructure.persistence.entity.RefreshTokenEntity;
import com.cinemesh.authservice.infrashtructure.persistence.entity.RoleEntity;
import com.cinemesh.authservice.infrashtructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrashtructure.persistence.repository.RefreshTokenRepository;
import com.cinemesh.authservice.infrashtructure.persistence.repository.RoleRepository;
import com.cinemesh.authservice.infrashtructure.persistence.repository.UserRepository;
import com.cinemesh.authservice.infrashtructure.security.CustomUserDetails;
import com.cinemesh.authservice.statics.UserStatus;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.dto.UserClaimsDto;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.security.JwtProperties;
import com.cinemesh.common.security.JwtService;
import com.cinemesh.common.statics.RoleName;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {


    ObjectMapper objectMapper;
    JwtService jwtService;
    JwtProperties jwtProperties;
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserSaveService userSaveService;
    AuthenticationManager authenticationManager;
    RefreshTokenRepository refreshTokenRepository;

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

    public LoginResponse login(@Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserEntity userEntity = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_EMAIL_NOT_FOUND));

        List<RoleDto> roleDtos = userEntity.getRoles().stream().map(r -> objectMapper.convertValue(r, RoleDto.class)).toList();
        UserClaimsDto userClaimsDto = UserClaimsDto.builder()
                .email(userEntity.getEmail())
                .userId(userEntity.getId().toString())
                .roles(roleDtos)
                .build();

        String accessToken = jwtService.generateJwtToken(userClaimsDto, false);
        String refreshToken = jwtService.generateJwtToken(userClaimsDto, true);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(userEntity)
                .expiryAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshToken().getTokenValidityMilliseconds() / 1000))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
