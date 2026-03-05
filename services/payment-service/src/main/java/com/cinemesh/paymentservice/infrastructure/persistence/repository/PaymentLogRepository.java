package com.cinemesh.paymentservice.infrastructure.persistence.repository;

import com.cinemesh.paymentservice.infrastructure.persistence.entity.PaymentLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentLogRepository extends JpaRepository<PaymentLogEntity, UUID> {
}
