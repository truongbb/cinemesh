package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SeatRepository extends JpaRepository<SeatEntity, UUID>, JpaSpecificationExecutor<SeatEntity> {
}
