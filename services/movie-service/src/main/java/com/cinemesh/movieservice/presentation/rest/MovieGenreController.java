package com.cinemesh.movieservice.presentation.rest;

import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.movieservice.application.dto.request.MovieGenreRequest;
import com.cinemesh.movieservice.application.dto.request.SearchMovieGenreRequest;
import com.cinemesh.movieservice.application.dto.response.MovieGenreResponse;
import com.cinemesh.movieservice.application.service.MovieGenreService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movie-genres")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieGenreController {

    MovieGenreService movieGenreService;

    @PostMapping
    public MovieGenreResponse createGenre(@RequestBody @Valid MovieGenreRequest request) {
        return movieGenreService.createGenre(request);
    }

    @GetMapping
    public CommonSearchResponse<MovieGenreResponse> searchGenre(SearchMovieGenreRequest request) {
        return movieGenreService.searchGenre(request);
    }

}
