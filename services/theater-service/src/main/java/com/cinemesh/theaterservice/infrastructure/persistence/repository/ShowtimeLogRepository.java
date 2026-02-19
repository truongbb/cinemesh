package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShowtimeLogRepository extends JpaRepository<ShowTimeLogEntity, UUID> {
}
