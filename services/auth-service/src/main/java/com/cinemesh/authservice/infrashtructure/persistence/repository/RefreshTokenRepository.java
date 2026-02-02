package com.cinemesh.authservice.infrashtructure.persistence.repository;

import com.cinemesh.authservice.infrashtructure.persistence.entity.RefreshTokenEntity;
import com.cinemesh.authservice.infrashtructure.persistence.entity.UserEntity;
import com.cinemesh.authservice.statics.RefreshTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    @Modifying
    @Query("update RefreshTokenEntity r set r.status = 'INACTIVE' where r.user.id = :userId")
    void logOut(UUID userId);

    Optional<RefreshTokenEntity> findByUserAndTokenAndStatus(UserEntity user, String token, RefreshTokenStatus status);


}
