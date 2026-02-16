package com.cinemesh.movieservice.presentation.rest;

import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.movieservice.application.dto.request.MovieRequest;
import com.cinemesh.movieservice.application.dto.request.SearchMovieRequest;
import com.cinemesh.movieservice.application.dto.request.UpdateMovieRequest;
import com.cinemesh.movieservice.application.dto.response.MovieResponse;
import com.cinemesh.movieservice.application.service.MovieService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {

    MovieService movieService;

    @PostMapping
    public MovieResponse createMovie(@RequestBody @Valid MovieRequest request) {
        return movieService.createMovie(request);
    }

    @PutMapping("/{id}")
    public MovieResponse updateMovie(@RequestBody @Valid UpdateMovieRequest request, @PathVariable UUID id) {
        return movieService.updateMovie(id, request);
    }

    @GetMapping
    public CommonSearchResponse<MovieResponse> searchMovie(SearchMovieRequest request) {
        return movieService.searchMovie(request);
    }

    @GetMapping("/{id}")
    public MovieResponse getMovieDetail(@PathVariable UUID id) {
        return movieService.getMovieDetail(id);
    }


}
