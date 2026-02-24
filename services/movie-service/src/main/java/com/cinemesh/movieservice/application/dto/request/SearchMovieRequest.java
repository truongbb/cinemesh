package com.cinemesh.movieservice.application.dto.request;

import com.cinemesh.common.dto.request.CommonSearchRequest;
import com.cinemesh.common.statics.MovieRated;
import com.cinemesh.common.statics.MovieStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchMovieRequest extends CommonSearchRequest {

    private String engTitle;

    private String vnTitle;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateFrom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDateTo;

    private String directors;

    private String actors;

    private List<MovieRated> rated;

    private List<UUID> genreIds;

    private List<MovieStatus> statuses;

    // phục vụ bên theater-service call qua
    private List<UUID> ids;

}
