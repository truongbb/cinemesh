package com.cinemesh.authservice.infrashtructure.persistence.repository;

import com.cinemesh.authservice.infrashtructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

}
