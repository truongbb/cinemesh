package com.cinemesh.paymentservice.domain.repository;

import com.cinemesh.paymentservice.domain.model.PaymentNotification;

public interface PaymentNotificationRepository {

    void save(PaymentNotification paymentNotification);

}
