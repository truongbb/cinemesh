package com.cinemesh.paymentservice.infrastructure.persistence.adapter;

import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.cinemesh.paymentservice.domain.model.Payment;
import com.cinemesh.paymentservice.domain.repository.PaymentRepository;
import com.cinemesh.paymentservice.infrastructure.persistence.entity.PaymentEntity;
import com.cinemesh.paymentservice.infrastructure.persistence.entity.PaymentLogEntity;
import com.cinemesh.paymentservice.infrastructure.persistence.repository.PaymentLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentPersistenceAdapter implements PaymentRepository {

    ObjectMapper objectMapper;
    com.cinemesh.paymentservice.infrastructure.persistence.repository.PaymentRepository paymentRepository;
    PaymentLogRepository paymentLogRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePayment(Payment payment) {
        try {
            PaymentLogEntity log = new PaymentLogEntity();
            log.setId(UUID.randomUUID());
            log.setPaymentId(payment.getId());
            log.setType(LogType.getByIsCreated(payment.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(payment));

            PaymentEntity entity = objectMapper.convertValue(payment, PaymentEntity.class);
            paymentRepository.save(entity);
            paymentLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }

}
