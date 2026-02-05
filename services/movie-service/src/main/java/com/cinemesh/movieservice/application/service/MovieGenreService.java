package com.cinemesh.movieservice.application.service;

import com.cinemesh.common.dto.MovieGenreDto;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.movieservice.application.dto.request.MovieGenreRequest;
import com.cinemesh.movieservice.application.dto.response.MovieGenreResponse;
import com.cinemesh.movieservice.domain.exception.MovieErrorCode;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MovieGenrePersistenceAdapter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieGenreService {

    MovieGenrePersistenceAdapter movieGenrePersistenceAdapter;

    public MovieGenreResponse createGenre(@Valid MovieGenreRequest request) {
        movieGenrePersistenceAdapter.findByName(request.getName())
                .ifPresent(genre -> {
                    throw new UnprocessableEntityException(MovieErrorCode.GENRE_EXISTED);
                });

        // build genre domain -> convert to entity -> save

        MovieGenreDto genreDto = MovieGenreDto.builder()
                .name(request.getName())
                .build();
        MovieGenre genre = new MovieGenre(genreDto);
        movieGenrePersistenceAdapter.saveGenre(genre);

        return MovieGenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

}
