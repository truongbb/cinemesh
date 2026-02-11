package com.cinemesh.movieservice.infrastructure.persistence.mapper;

import com.cinemesh.common.dto.MovieGenreDto;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieGenreMapper {


    ObjectMapper objectMapper;

    public MovieGenre convertToDomain(MovieGenreEntity entity) {
        if (entity == null) {
            return null;
        }
        MovieGenreDto dto = objectMapper.convertValue(entity, MovieGenreDto.class);
        return new MovieGenre(dto);
    }

    public MovieGenreDto convertToDto(MovieGenre domain) {
        if (domain == null) {
            return null;
        }
        return objectMapper.convertValue(domain, MovieGenreDto.class);
    }

}
