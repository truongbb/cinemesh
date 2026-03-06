package com.cinemesh.notificationservice.event.service;

import com.cinemesh.common.dto.domain.DomainEventDto;
import com.cinemesh.common.dto.domain.OrderDomainDto;
import com.cinemesh.common.event.domain.CinemeshEventName;
import com.cinemesh.common.event.kafka.dto.KafkaMessageDto;
import com.cinemesh.common.event.kafka.dto.KafkaMessagePayloadDto;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.notificationservice.email.EmailService;
import com.cinemesh.notificationservice.event.dto.OrderLogDto;
import com.cinemesh.notificationservice.event.dto.UserLogDto;
import com.cinemesh.notificationservice.exception.NotificationErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderLogEventKafkaService {

    @Resource(name = "snakeCaseObjectMapper")
    ObjectMapper snakeCaseObjectMapper;

    @Resource(name = "jsonOutputConverter")
    ObjectMapper objectMapper;

    EmailService emailService;


    public void handleEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment,
                            String groupId, boolean ignored) throws JsonProcessingException {
        log.info("Received order-log from kafka: {}", record);
        OrderLogDto orderLogDto = selectMessage(record);
        if (orderLogDto != null) handleOrderLog(orderLogDto);

//        kafkaMessageLogService.saveKafkaMessage(record, KafkaMessageStatus.SUCCEED, null, groupId, ignored);
        acknowledgment.acknowledge();
    }

    private OrderLogDto selectMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        KafkaMessageDto<OrderLogDto> eventMessage = snakeCaseObjectMapper.readValue(consumerRecord.value(), new TypeReference<>() {
        });
        KafkaMessagePayloadDto<OrderLogDto> eventMessagePayload = eventMessage.getPayload();
        if (!eventMessagePayload.getOp().equals("r") && !eventMessagePayload.getOp().equals("c")) {
            return null;
        }
        OrderLogDto detailDTO = eventMessagePayload.getAfter();
        if (ObjectUtils.isEmpty(detailDTO)) {
            return null;
        }
        return detailDTO;
    }

    public void handleOrderLog(OrderLogDto event) throws JsonProcessingException {
        OrderDomainDto orderDomainDto = objectMapper.readValue(event.getDetail(), OrderDomainDto.class);
        List<DomainEventDto> events = orderDomainDto.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        List<String> eventNames = events.stream().map(DomainEventDto::getName).toList();

        eventNames.stream()
                .filter(eventName -> eventName.equals(CinemeshEventName.ORDER_SUCCEED.name()))
                .findFirst()
                .ifPresent(eventName -> {
                    try {
                        emailService.sendOrderCompletedEmail(orderDomainDto);
                    } catch (MessagingException e) {
                        log.error("Unable to send order completed to user with event {}", eventName);
                        log.error(e.getMessage(), e);
                        throw new UnprocessableEntityException(NotificationErrorCode.SEND_ORDER_COMPLETED_EMAIL_FAILED);
                    }
                });
    }
}

