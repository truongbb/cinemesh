package com.cinemesh.bookingservice.infrastructure.persistence.mapper;

import com.cinemesh.bookingservice.application.dto.response.OrderResponse;
import com.cinemesh.bookingservice.domain.model.Order;
import com.cinemesh.bookingservice.infrastructure.persistence.entity.OrderEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderMapper {


    ObjectMapper objectMapper;

    public OrderResponse convertFromDomainToResponse(Order order) {
        return objectMapper.convertValue(order, OrderResponse.class);
    }

    public Order convertFromEntityToDomain(OrderEntity order) {
        return objectMapper.convertValue(order, Order.class);
    }

}
