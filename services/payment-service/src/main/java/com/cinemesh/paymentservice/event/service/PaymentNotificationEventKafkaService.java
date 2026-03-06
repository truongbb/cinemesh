package com.cinemesh.paymentservice.event.service;

import com.cinemesh.common.event.domain.CinemeshEvent;
import com.cinemesh.common.event.domain.CinemeshEventName;
import com.cinemesh.common.event.kafka.dto.KafkaMessageDto;
import com.cinemesh.common.event.kafka.dto.KafkaMessagePayloadDto;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.statics.PaymentStatus;
import com.cinemesh.paymentservice.application.dto.PaymentNotificationDto;
import com.cinemesh.paymentservice.domain.exception.PaymentErrorCode;
import com.cinemesh.paymentservice.domain.model.Payment;
import com.cinemesh.paymentservice.infrastructure.persistence.adapter.PaymentPersistenceAdapter;
import com.cinemesh.paymentservice.statics.PaymentNotificationStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentNotificationEventKafkaService {

    @Resource(name = "snakeCaseObjectMapper")
    ObjectMapper snakeCaseObjectMapper;

    @Resource(name = "jsonOutputConverter")
    ObjectMapper objectMapper;

    PaymentPersistenceAdapter paymentPersistenceAdapter;

    public void handleEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment,
                            String groupId, boolean ignored) throws JsonProcessingException {
        log.info("Received payment-notification from kafka: {}", record);
        PaymentNotificationDto paymentNotificationDto = selectMessage(record);
        if (paymentNotificationDto != null) handleUserLog(paymentNotificationDto);

//        kafkaMessageLogService.saveKafkaMessage(record, KafkaMessageStatus.SUCCEED, null, groupId, ignored);
        acknowledgment.acknowledge();
    }

    private PaymentNotificationDto selectMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        KafkaMessageDto<PaymentNotificationDto> eventMessage = snakeCaseObjectMapper.readValue(consumerRecord.value(), new TypeReference<>() {
        });
        KafkaMessagePayloadDto<PaymentNotificationDto> eventMessagePayload = eventMessage.getPayload();
        if (!eventMessagePayload.getOp().equals("r") && !eventMessagePayload.getOp().equals("c")) {
            return null;
        }
        PaymentNotificationDto detailDTO = eventMessagePayload.getAfter();
        if (ObjectUtils.isEmpty(detailDTO)) {
            return null;
        }
        return detailDTO;
    }

    public void handleUserLog(PaymentNotificationDto dto) throws JsonProcessingException {
        if (dto == null || PaymentNotificationStatus.UNPROCESSED.equals(dto.getStatus())) return;

        Map<String, String> vnPayParams = snakeCaseObjectMapper.readValue(dto.getRawPayload(), new TypeReference<>() {
        });
        UUID paymentId = UUID.fromString(vnPayParams.get("vnp_TxnRef"));
        BigDecimal paidAmount = new BigDecimal(vnPayParams.get("vnp_Amount"));
        Payment payment = paymentPersistenceAdapter.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(PaymentErrorCode.INVALID_ORDER_STATUS_FOR_PAYMENT));

        if (PaymentNotificationStatus.FAILED.equals(dto.getStatus())) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.addEvent(new CinemeshEvent(CinemeshEventName.PAYMENT_FAILED));
            paymentPersistenceAdapter.savePayment(payment);
            return;
        }

        payment.setPaidAmount(paidAmount);
        payment.setStatus(PaymentStatus.PAID);
        payment.addEvent(new CinemeshEvent(CinemeshEventName.PAYMENT_PAID));
        paymentPersistenceAdapter.savePayment(payment);
    }

}

