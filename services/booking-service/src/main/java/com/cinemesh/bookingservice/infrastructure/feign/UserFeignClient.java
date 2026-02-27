package com.cinemesh.bookingservice.infrastructure.feign;

import com.cinemesh.bookingservice.infrastructure.configuration.FeignConfiguration;
import com.cinemesh.bookingservice.infrastructure.feign.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "auth-service",
        url = "${feign.domain.auth-service}",
        configuration = FeignConfiguration.class
)
public interface UserFeignClient {

    @GetMapping("/api/v1/users/email")
    UserResponse getUserByEmail(@RequestParam String email);

}
