package com.cinemesh.movieservice.application.dto.request;

import com.cinemesh.common.dto.request.CommonSearchRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchMovieGenreRequest extends CommonSearchRequest {

    String name;

}
