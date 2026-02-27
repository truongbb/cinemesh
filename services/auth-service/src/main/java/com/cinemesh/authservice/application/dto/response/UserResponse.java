package com.cinemesh.authservice.application.dto.response;

import com.cinemesh.common.statics.UserStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    UUID id;
    String email;
    String fullName;
    String phone;
    LocalDate dob;
    String gender;
    String avatarUrl;
    UserStatus status;
    Set<RoleResponse> roles;

}
