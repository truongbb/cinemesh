package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ShowtimeRepository extends JpaRepository<ShowTimeEntity, UUID>, JpaSpecificationExecutor<ShowTimeEntity> {
}
