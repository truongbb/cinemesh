package com.cinemesh.paymentservice.domain.repository;

import com.cinemesh.paymentservice.domain.model.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    void savePayment(Payment payment);

    Optional<Payment> findById(UUID id);

}
