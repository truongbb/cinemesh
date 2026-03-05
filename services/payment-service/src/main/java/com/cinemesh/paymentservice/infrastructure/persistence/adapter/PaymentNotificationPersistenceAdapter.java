package com.cinemesh.paymentservice.infrastructure.persistence.adapter;

import com.cinemesh.paymentservice.domain.model.PaymentNotification;
import com.cinemesh.paymentservice.domain.repository.PaymentNotificationRepository;
import com.cinemesh.paymentservice.infrastructure.persistence.entity.PaymentNotificationEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentNotificationPersistenceAdapter implements PaymentNotificationRepository {

    ObjectMapper objectMapper;
    com.cinemesh.paymentservice.infrastructure.persistence.repository.PaymentNotificationRepository paymentNotificationRepository;


    @Override
    public void save(PaymentNotification paymentNotification) {
        paymentNotificationRepository.save(objectMapper.convertValue(paymentNotification, PaymentNotificationEntity.class));
    }
}
