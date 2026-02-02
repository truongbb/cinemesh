package com.cinemesh.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {

    String email;
    List<SimpleGrantedAuthority> authorities;

}
