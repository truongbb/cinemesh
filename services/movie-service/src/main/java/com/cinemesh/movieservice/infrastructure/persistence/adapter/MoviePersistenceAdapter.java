package com.cinemesh.movieservice.infrastructure.persistence.adapter;

import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.cinemesh.movieservice.domain.model.Movie;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieEntity;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieLogEntity;
import com.cinemesh.movieservice.infrastructure.persistence.mapper.MovieMapper;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieLogRepository;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MoviePersistenceAdapter implements com.cinemesh.movieservice.domain.repository.MovieRepository {

    ObjectMapper objectMapper;
    MovieMapper movieMapper;
    MovieRepository movieRepository;
    MovieLogRepository movieLogRepository;

    @Override
    public Optional<Movie> findById(UUID id) {
        return movieRepository.findById(id)
                .map(movieMapper::convertToDomain);
    }

//    @Override
//    public Optional<Movie> findByName(String name) {
//        return movieRepository.findByName(name)
//                .map(movieMapper::convertToDomain);
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMovie(Movie movie) {
        try {
            MovieLogEntity log = new MovieLogEntity();
            log.setId(UUID.randomUUID());
            log.setMovieId(movie.getId());
            log.setType(LogType.getByIsCreated(movie.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(movie));

            MovieEntity entity = objectMapper.convertValue(movie, MovieEntity.class);
            movieRepository.save(entity);
            movieLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }


    }

}
