package com.cinemesh.authservice.infrastructure.persistence.adapter;

import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrastructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrastructure.persistence.entity.UserLogEntity;
import com.cinemesh.authservice.infrastructure.persistence.mapper.UserMapper;
import com.cinemesh.authservice.infrastructure.persistence.repository.UserLogRepository;
import com.cinemesh.authservice.infrastructure.persistence.repository.UserRepository;
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

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPersistenceAdapter implements com.cinemesh.authservice.domain.repository.UserRepository {

    UserRepository userRepository;
    UserLogRepository userLogRepository;
    UserMapper userMapper;
    ObjectMapper objectMapper;

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::convertToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::convertToDomain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
