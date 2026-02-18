package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<RoomEntity, UUID>, JpaSpecificationExecutor<RoomEntity> {

    Optional<RoomEntity> findByName(String name);

}
