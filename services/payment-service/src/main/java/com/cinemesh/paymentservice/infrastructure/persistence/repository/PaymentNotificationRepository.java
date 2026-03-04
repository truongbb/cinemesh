package com.cinemesh.paymentservice.infrastructure.persistence.repository;

import com.cinemesh.paymentservice.infrastructure.persistence.entity.PaymentNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentNotificationRepository extends JpaRepository<PaymentNotificationEntity, UUID> {
}
