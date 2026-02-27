package com.cinemesh.bookingservice.domain.repository;

import com.cinemesh.bookingservice.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID id);

    void saveOrder(Order order);

    List<Order> findExpiredOrdersForProcessing(int timeToLiveInSecs);

    void saveAllOrders(List<Order> expiredOrders);

}
