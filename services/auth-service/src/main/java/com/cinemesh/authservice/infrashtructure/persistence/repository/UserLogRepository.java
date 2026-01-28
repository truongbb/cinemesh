package com.cinemesh.authservice.infrashtructure.persistence.repository;

import com.cinemesh.authservice.infrashtructure.persistence.entity.UserLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserLogRepository extends JpaRepository<UserLogEntity, UUID> {
}
