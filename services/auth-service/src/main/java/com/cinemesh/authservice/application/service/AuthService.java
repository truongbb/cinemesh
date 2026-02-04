package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.application.dto.request.LoginRequest;
import com.cinemesh.authservice.application.dto.request.RefreshTokenRequest;
import com.cinemesh.authservice.application.dto.request.RegisterRequest;
import com.cinemesh.authservice.application.dto.response.AuthenticationTokenResponse;
import com.cinemesh.authservice.application.dto.response.RegisterResponse;
import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrastructure.persistence.adapter.UserPersistenceAdapter;
import com.cinemesh.authservice.infrastructure.persistence.entity.RefreshTokenEntity;
import com.cinemesh.authservice.infrastructure.persistence.entity.RoleEntity;
import com.cinemesh.authservice.infrastructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrastructure.persistence.repository.RefreshTokenRepository;
import com.cinemesh.authservice.infrastructure.persistence.repository.RoleRepository;
import com.cinemesh.authservice.infrastructure.persistence.repository.UserRepository;
import com.cinemesh.authservice.infrastructure.security.CustomUserDetails;
import com.cinemesh.authservice.statics.RefreshTokenStatus;
import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.dto.UserClaimsDto;
import com.cinemesh.common.dto.UserDetailsDto;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.security.JwtProperties;
import com.cinemesh.common.security.JwtService;
import com.cinemesh.common.security.SecurityUtils;
import com.cinemesh.common.statics.RoleName;
import com.cinemesh.common.statics.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    UserPersistenceAdapter userPersistenceAdapter;
    AuthenticationManager authenticationManager;
    RefreshTokenRepository refreshTokenRepository;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnprocessableEntityException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = buildNewUser(request);
        userPersistenceAdapter.saveUser(user);

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

    public AuthenticationTokenResponse login(@Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserEntity userEntity = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_EMAIL_NOT_FOUND));

        UserClaimsDto userClaimsDto = buildUserClaimsDto(userEntity);

        String accessToken = jwtService.generateJwtToken(userClaimsDto, false);
        String refreshToken = jwtService.generateJwtToken(userClaimsDto, true);
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(userEntity)
                .status(RefreshTokenStatus.ACTIVE)
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return AuthenticationTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout() {
        String email = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_EMAIL_NOT_FOUND));
        refreshTokenRepository.logOut(userEntity.getId());
        SecurityContextHolder.clearContext();
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthenticationTokenResponse refresh(RefreshTokenRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto userDetails = (UserDetailsDto) authentication.getPrincipal();
        AuthenticationTokenResponse response = userRepository.findByEmail(userDetails.getEmail())
                .flatMap(user -> refreshTokenRepository
                        .findByUserAndTokenAndStatus(user, request.getRefreshToken(), RefreshTokenStatus.INACTIVE)
                        .map(oldRefreshToken -> {
                            LocalDateTime createdDateTime = LocalDateTime.ofInstant(oldRefreshToken.getCreatedAt(), ZoneId.systemDefault());
                            LocalDateTime expiryTime = createdDateTime.plusSeconds(jwtProperties.getRefreshToken().getTokenValidityMilliseconds() / 1000);
                            if (expiryTime.isBefore(LocalDateTime.now())) {
                                oldRefreshToken.setStatus(RefreshTokenStatus.INACTIVE);
                                refreshTokenRepository.save(oldRefreshToken);
                                return null;
                            }
                            UserClaimsDto userClaimsDto = buildUserClaimsDto(user);
                            String jwtToken = jwtService.generateJwtToken(userClaimsDto, false);
                            String refreshToken = jwtService.generateJwtToken(userClaimsDto, true);
                            RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                                    .token(refreshToken)
                                    .user(user)
                                    .status(RefreshTokenStatus.ACTIVE)
                                    .build();
                            refreshTokenRepository.save(refreshTokenEntity);
                            oldRefreshToken.setStatus(RefreshTokenStatus.INACTIVE);
                            refreshTokenRepository.save(oldRefreshToken);
                            return AuthenticationTokenResponse.builder()
                                    .accessToken(jwtToken)
                                    .refreshToken(refreshToken)
                                    .build();
                        }))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (response == null) {
            throw new UnprocessableEntityException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
        return response;
    }

    private UserClaimsDto buildUserClaimsDto(UserEntity userEntity) {
        if (userEntity == null) return null;
        List<RoleDto> roleDtos = userEntity.getRoles()
                .stream()
                .map(r -> objectMapper.convertValue(r, RoleDto.class))
                .toList();
        return UserClaimsDto.builder()
                .email(userEntity.getEmail())
                .userId(userEntity.getId().toString())
                .roles(roleDtos)
                .build();
    }

}
