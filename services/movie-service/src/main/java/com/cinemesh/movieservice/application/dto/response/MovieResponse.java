package com.cinemesh.movieservice.application.dto.response;

import com.cinemesh.common.statics.MovieRated;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponse {

    UUID id;
    String engTitle;
    String vnTitle;
    String description;
    int durationMinutes;
    LocalDate releaseDate;
    String posterUrl;
    String trailerUrl;
    String directors;
    String actors;
    MovieRated rated;
    List<MovieGenreResponse> genres;

}
