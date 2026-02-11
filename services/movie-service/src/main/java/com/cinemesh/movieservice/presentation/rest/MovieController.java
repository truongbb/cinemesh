package com.cinemesh.movieservice.presentation.rest;

import com.cinemesh.movieservice.application.dto.request.MovieRequest;
import com.cinemesh.movieservice.application.dto.response.MovieResponse;
import com.cinemesh.movieservice.application.service.MovieService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

//    @PutMapping
//    public MovieResponse updateMovie(@RequestBody @Valid MovieRequest request) {
//        return movieService.createMovie(request);
//    }


}
