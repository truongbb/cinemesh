package com.cinemesh.common.dto;

import com.cinemesh.common.statics.MovieRated;
import com.cinemesh.common.statics.MovieStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {

    private UUID id;
    private String engTitle;
    private String vnTitle;
    private String description;
    private int durationMinutes;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private String directors;
    private String actors;
    private MovieRated rated;
    private Set<MovieGenreDto> genres;
    private MovieStatus status;
}
