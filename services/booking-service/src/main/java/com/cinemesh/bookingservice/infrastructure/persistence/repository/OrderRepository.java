package com.cinemesh.bookingservice.infrastructure.persistence.repository;

import com.cinemesh.bookingservice.infrastructure.persistence.entity.OrderEntity;
import com.cinemesh.bookingservice.statics.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {

    List<OrderEntity> findByStatusAndCreatedAtLessThanEqual(OrderStatus status, Instant timeStone);

}
