package com.cinemesh.movieservice.application.service;

import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.movieservice.application.dto.request.MovieRequest;
import com.cinemesh.movieservice.application.dto.response.MovieResponse;
import com.cinemesh.movieservice.domain.exception.MovieErrorCode;
import com.cinemesh.movieservice.domain.model.Movie;
import com.cinemesh.movieservice.domain.model.MovieGenre;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MovieGenrePersistenceAdapter;
import com.cinemesh.movieservice.infrastructure.persistence.adapter.MoviePersistenceAdapter;
import com.cinemesh.movieservice.infrastructure.persistence.mapper.MovieMapper;
import com.cinemesh.movieservice.infrastructure.persistence.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

//    public CommonSearchResponse<MovieResponse> searchGenre(SearchMovieGenreRequest request) {
//        PageRequest pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());
//
////        Specification<MovieGenreEntity> spec = Specification.where(null); // Start empty
//        Specification<MovieGenreEntity> spec = (root, query, cb) -> cb.conjunction();
//        if (request.getName() != null) {
//            spec = spec.and(MovieGenreSpecification.hasName(request.getName()));
//        }
//        Page<MovieGenreEntity> pageResult = movieRepository.findAll(spec, pageable);
//
//        return CommonSearchResponse.<MovieGenreResponse>builder()
//                .data(
//                        pageResult.getContent()
//                                .stream()
//                                .map(entity -> objectMapper.convertValue(entity, MovieGenreResponse.class))
//                                .toList()
//                )
//                .pagination(
//                        CommonSearchResponse.PaginationResponse.builder()
//                                .pageSize(request.getPageSize())
//                                .pageIndex(request.getPageIndex())
//                                .totalRecords(pageResult.getTotalElements())
//                                .totalPage(pageResult.getTotalPages())
//                                .build()
//                )
//                .build();
//    }
//
//    public MovieResponse getGenreDetail(UUID id) {
//        MovieGenre genre = moviePersistenceAdapter.findById(id)
//                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
//        return objectMapper.convertValue(genre, MovieResponse.class);
//    }
//
//    public MovieResponse updateGenre(UUID id, @Valid MovieRequest request) {
//        Movie genre = moviePersistenceAdapter.findById(id)
//                .orElseThrow(() -> new NotFoundException(MovieErrorCode.GENRE_NOT_FOUND));
//        genre.setName(request.getName());
//        moviePersistenceAdapter.saveMovie(genre);
//        return objectMapper.convertValue(genre, MovieResponse.class);
//    }


}
