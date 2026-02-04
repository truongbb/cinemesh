package com.cinemesh.authservice.domain.repository;

import com.cinemesh.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    void saveUser(User user);

}
