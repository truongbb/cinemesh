package com.cinemesh.bookingservice.infrastructure.persistence.repository;

import com.cinemesh.bookingservice.infrastructure.persistence.entity.OrderLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderLogRepository extends JpaRepository<OrderLogEntity, UUID> {
}
