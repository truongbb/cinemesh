package com.cinemesh.paymentservice.domain.repository;

import com.cinemesh.paymentservice.domain.model.Payment;

public interface PaymentRepository {

    void savePayment(Payment payment);

}
