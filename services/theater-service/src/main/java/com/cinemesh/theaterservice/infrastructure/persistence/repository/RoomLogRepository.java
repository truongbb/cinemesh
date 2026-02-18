package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoomLogRepository extends JpaRepository<RoomLogEntity, UUID> {
}
