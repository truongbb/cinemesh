package com.cinemesh.paymentservice.infrastructure.feign;

import com.cinemesh.common.config.FeignClientConfig;
import com.cinemesh.paymentservice.infrastructure.feign.response.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "booking-service",
        url = "${feign.domain.booking-service}",
        configuration = FeignClientConfig.class
)
public interface BookingFeignClient {

    @GetMapping("/api/v1/bookings/{id}")
    OrderResponse getOrderDetails(@PathVariable UUID id);

}
