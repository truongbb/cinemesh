package com.cinemesh.authservice.infrastructure.persistence.mapper;

import com.cinemesh.authservice.domain.model.User;
import com.cinemesh.authservice.infrastructure.persistence.entity.UserEntity;
import com.cinemesh.common.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserMapper {

    ObjectMapper objectMapper;

    public User convertToDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserDto userDto = objectMapper.convertValue(userEntity, UserDto.class);
        return new User(userDto);
    }

}
