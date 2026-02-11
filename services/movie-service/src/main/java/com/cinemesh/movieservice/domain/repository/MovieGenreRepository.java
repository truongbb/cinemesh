package com.cinemesh.movieservice.domain.repository;

import com.cinemesh.movieservice.domain.model.MovieGenre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MovieGenreRepository {

    Optional<MovieGenre> findById(UUID id);

    List<MovieGenre> findAllByIds(List<UUID> ids);

    Optional<MovieGenre> findByName(String name);

    void saveGenre(MovieGenre genre);

    void deleteGenre(MovieGenre genre);

}
