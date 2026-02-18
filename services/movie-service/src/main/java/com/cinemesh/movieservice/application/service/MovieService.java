package com.cinemesh.movieservice.application.service;

import com.cinemesh.movieservice.application.dto.MovieDto;
import com.cinemesh.movieservice.application.dto.MovieGenreDto;
import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.movieservice.application.dto.request.MovieRequest;
import com.cinemesh.movieservice.application.dto.request.SearchMovieRequest;
import com.cinemesh.movieservice.application.dto.request.UpdateMovieRequest;
import com.cinemesh.movieservice.application.dto.response.MovieResponse;
import com.cinemesh.movieservice.domain.exception.MovieErrorCode;
import com.cinemesh.movieservice.domain.model.Movie;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MovieGenrePersistenceAdapter;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MoviePersistenceAdapter;
import com.cinemesh.movieservice.infrastructure.persistence.entity.MovieEntity;
import com.cinemesh.movieservice.infrastructure.persistence.mapper.MovieMapper;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieRepository;
import com.cinemesh.movieservice.infrastructure.persistence.specification.MovieSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {

    ObjectMapper objectMapper;
    MovieRepository movieRepository;
    MovieGenrePersistenceAdapter movieGenrePersistenceAdapter;
    MoviePersistenceAdapter moviePersistenceAdapter;
    MovieMapper movieMapper;

    public MovieResponse createMovie(@Valid MovieRequest request) {
        List<MovieGenre> genres = CollectionUtils.isEmpty(request.getGenreIds()) ? new ArrayList<>()
                : movieGenrePersistenceAdapter.findAllByIds(request.getGenreIds());

        if (CollectionUtils.isEmpty(genres)) {
            throw new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND);
        }

        Movie movie = new Movie(movieMapper.convertRequestToDto(request, genres));
        moviePersistenceAdapter.saveMovie(movie);

        return movieMapper.convertDomainToResponse(movie);
    }

    public MovieResponse updateMovie(UUID id, @Valid UpdateMovieRequest request) {
        Movie movie = moviePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(MovieErrorCode.MOVIE_NOT_FOUND));

        MovieDto movieDto = objectMapper.convertValue(request, MovieDto.class);
        movieDto.setId(id);
        List<MovieGenre> genres = movieGenrePersistenceAdapter.findAllByIds(request.getGenreIds());
        if (CollectionUtils.isEmpty(genres)) {
            throw new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND);
        }
        List<MovieGenreDto> movieGenreDtos = genres
                .stream()
                .map(genre -> MovieGenreDto.builder().id(genre.getId()).name(genre.getName()).build())
                .toList();
        movieDto.setGenres(new HashSet<>(movieGenreDtos));

        movie.update(movieDto);
        moviePersistenceAdapter.saveMovie(movie);
        return objectMapper.convertValue(movie, MovieResponse.class);
    }

    public CommonSearchResponse<MovieResponse> searchMovie(SearchMovieRequest request) {
        PageRequest pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());

        Specification<MovieEntity> spec = MovieSpecification.search(request);

        Page<MovieEntity> pageResult = movieRepository.findAll(spec, pageable);

        return CommonSearchResponse.<MovieResponse>builder()
                .data(
                        pageResult.getContent()
                                .stream()
                                .map(movieMapper::convertEntityToResponse)
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

    public MovieResponse getMovieDetail(UUID id) {
        Movie movie = moviePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
        return objectMapper.convertValue(movie, MovieResponse.class);
    }

}
