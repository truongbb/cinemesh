package com.cinemesh.authservice.application.service;

import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrashtructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrashtructure.persistence.entity.UserLogEntity;
import com.cinemesh.authservice.infrashtructure.persistence.repository.UserLogRepository;
import com.cinemesh.authservice.infrashtructure.persistence.repository.UserRepository;
import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSaveService {

    UserRepository userRepository;
    UserLogRepository userLogRepository;
    ObjectMapper objectMapper;

    @Transactional
    public void saveUser(User user) {
        try {
            UserLogEntity log = new UserLogEntity();
            log.setId(UUID.randomUUID());
            log.setUserId(user.getId());
            log.setType(LogType.getByIsCreated(user.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(user));

            UserEntity userEntity = objectMapper.convertValue(user, UserEntity.class);
            userRepository.save(userEntity);
            userLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }

}
