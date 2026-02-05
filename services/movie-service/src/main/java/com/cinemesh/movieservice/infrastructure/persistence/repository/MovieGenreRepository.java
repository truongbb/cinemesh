package com.cinemesh.movieservice.infrastructure.persistence.repository;

import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieGenreRepository extends JpaRepository<MovieGenreEntity, UUID> {

    Optional<MovieGenreEntity> findByName(String name);

}
