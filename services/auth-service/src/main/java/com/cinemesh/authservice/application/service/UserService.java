package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.application.dto.response.UserResponse;
import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrastructure.persistence.adapter.UserPersistenceAdapter;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.UserStatus;
import com.cinemesh.common.utils.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    ObjectMapper objectMapper;
    UserPersistenceAdapter userPersistenceAdapter;

    public void activateUser(@NotNull(message = "User id must not be null") UUID id) {
        User user = userPersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.USER_NOT_FOUND));

        if (ObjectUtils.equals(user.getStatus(), UserStatus.ACTIVE)) {
            throw new UnprocessableEntityException(AuthErrorCode.USER_ALREADY_ACTIVATED);
        }

        user.setStatus(UserStatus.ACTIVE);
        user.addEvent(new CinemeshEvent(CinemeshEventName.USER_ACTIVATED));
        userPersistenceAdapter.saveUser(user);
    }

    public UserResponse getUserByEmail(String email) {
        return userPersistenceAdapter.findByEmail(email)
                .map(user -> objectMapper.convertValue(user, UserResponse.class))
                .orElse(null);
    }

}
