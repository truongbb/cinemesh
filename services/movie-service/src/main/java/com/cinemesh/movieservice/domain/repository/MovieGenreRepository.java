package com.cinemesh.movieservice.domain.repository;

import com.cinemesh.movieservice.domain.model.MovieGenre;

import java.util.Optional;
import java.util.UUID;

public interface MovieGenreRepository {

    Optional<MovieGenre> findById(UUID id);

    Optional<MovieGenre> findByName(String name);

    void saveGenre(MovieGenre genre);

}
