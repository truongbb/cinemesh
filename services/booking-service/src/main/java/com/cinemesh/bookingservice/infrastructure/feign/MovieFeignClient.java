package com.cinemesh.bookingservice.infrastructure.feign;

import com.cinemesh.bookingservice.infrastructure.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "movie-service",
        url = "${feign.domain.movie-service}",
        configuration = FeignConfiguration.class
)
public interface MovieFeignClient {

//    @GetMapping("/api/v1/movies/{id}")
//    MovieResponse getMovieDetails(@PathVariable UUID id);
//
//    @GetMapping("/api/v1/movies")
//    CommonSearchResponse<MovieResponse> search(@RequestParam("ids") List<UUID> ids);

}
