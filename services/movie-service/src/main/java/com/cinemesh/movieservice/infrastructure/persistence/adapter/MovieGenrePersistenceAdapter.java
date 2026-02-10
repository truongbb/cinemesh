package com.cinemesh.movieservice.infrastructure.persistence.adapter;

import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import com.cinemesh.movieservice.infrastructure.persistence.mapper.MovieGenreMapper;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieGenreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieGenrePersistenceAdapter implements com.cinemesh.movieservice.domain.repository.MovieGenreRepository {

    ObjectMapper objectMapper;
    MovieGenreMapper movieGenreMapper;
    MovieGenreRepository movieGenreRepository;

    @Override
    public Optional<MovieGenre> findById(UUID id) {
        return movieGenreRepository.findById(id)
                .map(movieGenreMapper::convertToDomain);
    }

    @Override
    public Optional<MovieGenre> findByName(String name) {
        return movieGenreRepository.findByName(name)
                .map(movieGenreMapper::convertToDomain);
    }

    @Override
    public void saveGenre(MovieGenre genre) {
        MovieGenreEntity entity = objectMapper.convertValue(genre, MovieGenreEntity.class);
        movieGenreRepository.save(entity);
    }

    @Override
    public void deleteGenre(MovieGenre genre) {
        MovieGenreEntity entity = objectMapper.convertValue(genre, MovieGenreEntity.class);
        movieGenreRepository.delete(entity);
    }
}
