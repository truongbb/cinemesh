package com.cinemesh.movieservice.infrastructure.persistence.repository;

import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, UUID>, JpaSpecificationExecutor<MovieGenreEntity> {

    Optional<MovieGenreEntity> findByName(String name);

}
