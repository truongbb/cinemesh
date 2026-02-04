package com.cinemesh.authservice.infrastructure.security;

import com.cinemesh.authservice.infrastructure.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final String email; // Dùng email làm username
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    // Factory method để convert từ Entity sang UserDetails
    public static CustomUserDetails mapFrom(UserEntity userEntity) {
        // Convert Role String sang GrantedAuthority của Spring
        // Giả sử RoleEntity có field name là "ROLE_ADMIN", "ROLE_CUSTOMER"...
        List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new CustomUserDetails(
                userEntity.getEmail(),
                userEntity.getPassword(),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Spring Security gọi là Username, nhưng ta trả về Email
    }

    // Mấy cái này tùy logic business, tạm thời trả về true hết
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    } // Hoặc check userEntity.getStatus() != LOCKED

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    } // Hoặc check userEntity.getStatus() == ACTIVE
}
