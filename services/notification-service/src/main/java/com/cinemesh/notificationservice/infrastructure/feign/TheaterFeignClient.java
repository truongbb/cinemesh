package com.cinemesh.notificationservice.infrastructure.feign;

import com.cinemesh.common.config.FeignClientConfig;
import com.cinemesh.notificationservice.infrastructure.feign.response.ShowtimeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "theater-service",
        url = "${feign.domain.theater-service}",
        configuration = FeignClientConfig.class
)
public interface TheaterFeignClient {

    @GetMapping("/api/v1/showtimes/{id}")
    ShowtimeResponse getShowtimeDetail(@PathVariable UUID id);


}
