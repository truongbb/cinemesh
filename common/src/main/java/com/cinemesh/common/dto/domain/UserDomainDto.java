package com.cinemesh.common.dto.domain;

import com.cinemesh.common.dto.RoleDto;
import com.cinemesh.common.statics.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDomainDto extends BaseDomainEntityDto<UUID> {

    private String email;
    private String password;
    private String fullName;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String avatarUrl;
    private UserStatus status;
    private Set<RoleDto> roles;
}
