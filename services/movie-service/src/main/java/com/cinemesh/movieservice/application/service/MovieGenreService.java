package com.cinemesh.movieservice.application.service;

import com.cinemesh.movieservice.application.dto.MovieGenreDto;
import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.movieservice.application.dto.request.MovieGenreRequest;
import com.cinemesh.movieservice.application.dto.request.SearchMovieGenreRequest;
import com.cinemesh.movieservice.application.dto.response.MovieGenreResponse;
import com.cinemesh.movieservice.domain.exception.MovieErrorCode;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MovieGenrePersistenceAdapter;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieGenreEntity;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieGenreRepository;
import com.cinemesh.movieservice.infrastructure.persistence.specification.MovieGenreSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieGenreService {

    ObjectMapper objectMapper;
    MovieGenreRepository movieGenreRepository;
    MovieGenrePersistenceAdapter movieGenrePersistenceAdapter;

    public MovieGenreResponse createGenre(@Valid MovieGenreRequest request) {
        movieGenrePersistenceAdapter.findByName(request.getName())
                .ifPresent(genre -> {
                    throw new UnprocessableEntityException(MovieErrorCode.GENRE_EXISTED);
                });

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

    public CommonSearchResponse<MovieGenreResponse> searchGenre(SearchMovieGenreRequest request) {
        PageRequest pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());

//        Specification<MovieGenreEntity> spec = Specification.where(null); // Start empty
        Specification<MovieGenreEntity> spec = (root, query, cb) -> cb.conjunction();
        if (request.getName() != null) {
            spec = spec.and(MovieGenreSpecification.hasName(request.getName()));
        }
        Page<MovieGenreEntity> pageResult = movieGenreRepository.findAll(spec, pageable);

        return CommonSearchResponse.<MovieGenreResponse>builder()
                .data(
                        pageResult.getContent()
                                .stream()
                                .map(entity -> objectMapper.convertValue(entity, MovieGenreResponse.class))
                                .toList()
                )
                .pagination(
                        CommonSearchResponse.PaginationResponse.builder()
                                .pageSize(request.getPageSize())
                                .pageIndex(request.getPageIndex())
                                .totalRecords(pageResult.getTotalElements())
                                .totalPage(pageResult.getTotalPages())
                                .build()
                )
                .build();
    }

    public MovieGenreResponse getGenreDetail(UUID id) {
        MovieGenre genre = movieGenrePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
        return objectMapper.convertValue(genre, MovieGenreResponse.class);
    }

    public MovieGenreResponse updateGenre(UUID id, @Valid MovieGenreRequest request) {
        MovieGenre genre = movieGenrePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
        genre.setName(request.getName());
        movieGenrePersistenceAdapter.saveGenre(genre);
        return objectMapper.convertValue(genre, MovieGenreResponse.class);
    }

    public void deleteGenre(UUID id) {
        MovieGenre genre = movieGenrePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
        movieGenrePersistenceAdapter.deleteGenre(genre);
    }

}
