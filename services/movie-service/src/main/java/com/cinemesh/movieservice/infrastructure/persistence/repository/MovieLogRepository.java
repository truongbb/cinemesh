package com.cinemesh.movieservice.infrastructure.persistence.repository;

import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieLogRepository extends JpaRepository<MovieLogEntity, UUID> {
}
