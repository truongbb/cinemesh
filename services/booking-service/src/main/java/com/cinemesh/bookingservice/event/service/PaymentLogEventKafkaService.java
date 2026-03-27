package com.cinemesh.bookingservice.event.service;

import com.cinemesh.bookingservice.application.service.BookingService;
import com.cinemesh.bookingservice.domain.exception.BookingErrorCode;
import com.cinemesh.bookingservice.event.dto.PaymentLogDto;
import com.cinemesh.common.dto.domain.DomainEventDto;
import com.cinemesh.common.dto.domain.PaymentDomainDto;
import com.cinemesh.common.event.domain.CinemeshEventName;
import com.cinemesh.common.event.kafka.dto.KafkaMessageDto;
import com.cinemesh.common.event.kafka.dto.KafkaMessagePayloadDto;
import com.cinemesh.common.exception.UnprocessableEntityException;
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
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentLogEventKafkaService {

    @Resource(name = "snakeCaseObjectMapper")
    ObjectMapper snakeCaseObjectMapper;

    @Resource(name = "jsonOutputConverter")
    ObjectMapper objectMapper;

    BookingService bookingService;

    public void handleEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment,
                            String groupId, boolean ignored) throws JsonProcessingException {
        log.info("Received payment-log from kafka: {}", record);
        PaymentLogDto paymentLogDto = selectMessage(record);
        if (paymentLogDto != null) handlePaymentLog(paymentLogDto);

//        kafkaMessageLogService.saveKafkaMessage(record, KafkaMessageStatus.SUCCEED, null, groupId, ignored);
        acknowledgment.acknowledge();
    }

    private PaymentLogDto selectMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        KafkaMessagePayloadDto<PaymentLogDto> eventMessagePayload = snakeCaseObjectMapper.readValue(consumerRecord.value(), new TypeReference<>() {
        });
//        KafkaMessagePayloadDto<PaymentLogDto> eventMessagePayload = eventMessage.getPayload();
        if (!eventMessagePayload.getOp().equals("r") && !eventMessagePayload.getOp().equals("c")) {
            return null;
        }
        PaymentLogDto detailDTO = eventMessagePayload.getAfter();
        if (ObjectUtils.isEmpty(detailDTO)) {
            return null;
        }
        return detailDTO;
    }

    public void handlePaymentLog(PaymentLogDto event) throws JsonProcessingException {
        PaymentDomainDto paymentDomainDto = objectMapper.readValue(event.getDetail(), PaymentDomainDto.class);
        List<DomainEventDto> events = paymentDomainDto.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        List<String> eventNames = events.stream().map(DomainEventDto::getName).toList();

        eventNames.stream()
                .filter(eventName ->
                        eventName.equals(CinemeshEventName.PAYMENT_FAILED.name())
                                || eventName.equals(CinemeshEventName.PAYMENT_PAID.name())
                )
                .findFirst()
                .ifPresent(eventName -> {
                    try {
                        bookingService.updateOrderStatus(paymentDomainDto);
                    } catch (MessagingException e) {
                        log.error("Unable to change status of order when receiving payment result {}", eventName);
                        log.error(e.getMessage(), e);
                        throw new UnprocessableEntityException(BookingErrorCode.ORDER_STATUS_UPDATE_FAILED);
                    }
                });
    }

}

