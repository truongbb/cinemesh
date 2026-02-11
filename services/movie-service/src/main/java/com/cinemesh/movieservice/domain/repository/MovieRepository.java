package com.cinemesh.movieservice.domain.repository;

import com.cinemesh.movieservice.domain.model.Movie;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository {

    Optional<Movie> findById(UUID id);

//    Optional<Movie> findByName(String name);

    void saveMovie(Movie genre);

}
