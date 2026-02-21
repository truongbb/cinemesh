package com.cinemesh.movieservice.application.dto.request;

import com.cinemesh.common.statics.MovieRated;
import com.cinemesh.common.statics.MovieStatus;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateMovieRequest {

    @NotBlank(message = "English title is required")
    private String engTitle;

    @NotBlank(message = "Vietnamese title is required")
    private String vnTitle;

    @NotBlank(message = "Description is required")
    @Length(max = 1000)
    private String description;

    @Positive(message = "Duration minutes must be greater than 0")
    private int durationMinutes;

    @NotNull(message = "Release date must not be null")
    @Future(message = "Release date must be future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotBlank(message = "Poster url is required")
    @Length(max = 1000)
    private String posterUrl;

    @NotBlank(message = "Trailer url is required")
    @Length(max = 1000)
    private String trailerUrl;

    @NotBlank(message = "Directors is required")
    @Length(max = 1000)
    private String directors;

    @NotBlank(message = "Actors is required")
    @Length(max = 1000)
    private String actors;

    @NotNull(message = "Rated is required")
    private MovieRated rated;

    @NotEmpty(message = "Movie genres is required")
    private List<UUID> genreIds;

    @NotNull(message = "Movie status is not null")
    private MovieStatus status;

}
