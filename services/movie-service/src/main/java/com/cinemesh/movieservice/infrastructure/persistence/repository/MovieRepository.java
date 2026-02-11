package com.cinemesh.movieservice.infrastructure.persistence.repository;

import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<MovieEntity, UUID>, JpaSpecificationExecutor<MovieEntity> {

//    Optional<MovieEntity> findByName(String name);

}
