package com.cinemesh.authservice.domain.model;

import com.cinemesh.authservice.domain.value_object.RoleName;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Role {
    private final Integer id;
    private final RoleName name;

}
