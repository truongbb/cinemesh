package com.cinemesh.authservice.infrashtructure.persistence.repository;

import com.cinemesh.authservice.infrashtructure.persistence.entity.RoleEntity;
import com.cinemesh.common.statics.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(RoleName name);

}
