package com.cinemesh.authservice.infrastructure.persistence.repository;

import com.cinemesh.authservice.infrastructure.persistence.entity.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserLogRepository extends JpaRepository<UserLogEntity, UUID> {
}
