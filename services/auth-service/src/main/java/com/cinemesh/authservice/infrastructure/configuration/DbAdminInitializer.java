package com.cinemesh.authservice.infrastructure.configuration;

import com.cinemesh.authservice.domain.exception.AuthErrorCode;
import com.cinemesh.authservice.infrastructure.persistence.entity.RoleEntity;
import com.cinemesh.authservice.infrastructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.infrastructure.persistence.repository.RoleRepository;
import com.cinemesh.authservice.infrastructure.persistence.repository.UserRepository;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.statics.RoleName;
import com.cinemesh.common.statics.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbAdminInitializer implements CommandLineRunner {

    @Value("${application.account.admin.email}")
    String adminEmail;

    @Value("${application.account.admin.password}")
    String adminPassword;

    final UserRepository userRepository;
    final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<UserEntity> userOptional = userRepository.findByEmail(adminEmail);
        if (userOptional.isPresent()) {
            return;
        }

        RoleEntity adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new NotFoundException(AuthErrorCode.ROLE_NOT_FOUND));

        UserEntity user = new UserEntity();
        user.setEmail(adminEmail);
        user.setPassword(adminPassword);
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(adminRole);
        user.setRoles(roles);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

}
