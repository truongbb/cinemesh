package com.cinemesh.bookingservice.infrastructure.persistence.adapter;

import com.cinemesh.bookingservice.domain.model.Order;
import com.cinemesh.bookingservice.infrastructure.persistence.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderPersistenceAdapter implements com.cinemesh.bookingservice.domain.repository.OrderRepository {

    OrderRepository orderRepository;

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.empty();
    }

}
