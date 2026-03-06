package com.cinemesh.notificationservice.infrastructure.feign;

import com.cinemesh.notificationservice.infrastructure.configuration.FeignConfiguration;
import com.cinemesh.notificationservice.infrastructure.feign.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "auth-service",
        url = "${feign.domain.auth-service}",
        configuration = FeignConfiguration.class
)
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponse getUserById(@PathVariable UUID id);

}
