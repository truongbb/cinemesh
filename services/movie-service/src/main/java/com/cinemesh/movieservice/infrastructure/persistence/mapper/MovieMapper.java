package com.cinemesh.movieservice.infrastructure.persistence.mapper;

import com.cinemesh.movieservice.application.dto.MovieDto;
import com.cinemesh.movieservice.statics.MovieStatus;
import com.cinemesh.movieservice.application.dto.request.MovieRequest;
import com.cinemesh.movieservice.application.dto.response.MovieResponse;
import com.cinemesh.movieservice.domain.model.Movie;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieMapper {

    ObjectMapper objectMapper;
    MovieGenreMapper movieGenreMapper;

    public Movie convertToDomain(MovieEntity entity) {
        if (entity == null) {
            return null;
        }
        MovieDto dto = objectMapper.convertValue(entity, MovieDto.class);
        return new Movie(dto);
    }

    public MovieDto convertRequestToDto(MovieRequest request, List<MovieGenre> genres) {
        if (request == null) return null;
        return MovieDto.builder()
                .engTitle(request.getEngTitle())
                .vnTitle(request.getVnTitle())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .trailerUrl(request.getTrailerUrl())
                .directors(request.getDirectors())
                .actors(request.getActors())
                .rated(request.getRated())
                .status(MovieStatus.COMING_SOON)
                .genres(genres.stream().map(movieGenreMapper::convertToDto).collect(Collectors.toSet()))
                .build();
    }

    public MovieResponse convertDomainToResponse(Movie domain) {
        if (domain == null) return null;
        return objectMapper.convertValue(domain, MovieResponse.class);
    }

    public MovieResponse convertEntityToResponse(MovieEntity domain) {
        if (domain == null) return null;
        return objectMapper.convertValue(domain, MovieResponse.class);
    }

}
