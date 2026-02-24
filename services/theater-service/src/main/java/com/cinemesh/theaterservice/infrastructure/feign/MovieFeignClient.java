package com.cinemesh.theaterservice.infrastructure.feign;

import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.theaterservice.application.dto.response.MovieResponse;
import com.cinemesh.theaterservice.infrastructure.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "movie-service",
        url = "${feign.domain.movie-service}",
        configuration = FeignConfiguration.class
)
public interface MovieFeignClient {

    @GetMapping("/api/v1/movies/{id}")
    MovieResponse getMovieDetails(@PathVariable UUID id);

    @GetMapping("/api/v1/movies")
    CommonSearchResponse<MovieResponse> search(@RequestParam("ids") List<UUID> ids);

}
