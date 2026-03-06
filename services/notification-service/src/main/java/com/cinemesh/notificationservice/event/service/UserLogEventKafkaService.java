package com.cinemesh.notificationservice.event.service;

import com.cinemesh.common.dto.domain.DomainEventDto;
import com.cinemesh.common.dto.domain.UserDomainDto;
import com.cinemesh.common.event.domain.CinemeshEventName;
import com.cinemesh.common.event.kafka.dto.KafkaMessagePayloadDto;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.notificationservice.email.EmailService;
import com.cinemesh.notificationservice.event.dto.UserLogDto;
import com.cinemesh.common.event.kafka.dto.KafkaMessageDto;
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
public class UserLogEventKafkaService {

    @Resource(name = "snakeCaseObjectMapper")
    ObjectMapper snakeCaseObjectMapper;

    @Resource(name = "jsonOutputConverter")
    ObjectMapper objectMapper;

    EmailService emailService;


    public void handleEvent(ConsumerRecord<String, String> record, Acknowledgment acknowledgment,
                            String groupId, boolean ignored) throws JsonProcessingException {
        log.info("Received user-log from kafka: {}", record);
        UserLogDto userLogDto = selectMessage(record);
        if (userLogDto != null) handleUserLog(userLogDto);

//        kafkaMessageLogService.saveKafkaMessage(record, KafkaMessageStatus.SUCCEED, null, groupId, ignored);
        acknowledgment.acknowledge();
    }

    private UserLogDto selectMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        KafkaMessageDto<UserLogDto> eventMessage = snakeCaseObjectMapper.readValue(consumerRecord.value(), new TypeReference<>() {
        });
        KafkaMessagePayloadDto<UserLogDto> eventMessagePayload = eventMessage.getPayload();
        if (!eventMessagePayload.getOp().equals("r") && !eventMessagePayload.getOp().equals("c")) {
            return null;
        }
        UserLogDto detailDTO = eventMessagePayload.getAfter();
        if (ObjectUtils.isEmpty(detailDTO)) {
            return null;
        }
        return detailDTO;
    }

    public void handleUserLog(UserLogDto event) throws JsonProcessingException {
        UserDomainDto userDomainDto = objectMapper.readValue(event.getDetail(), UserDomainDto.class);
        List<DomainEventDto> events = userDomainDto.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        List<String> eventNames = events.stream().map(DomainEventDto::getName).toList();

        eventNames.stream()
                .filter(eventName -> eventName.equals(CinemeshEventName.USER_CREATED.name()))
                .findFirst()
                .ifPresent(eventName -> {
                    try {
                        emailService.sendActivationMail(userDomainDto);
                    } catch (MessagingException e) {
                        log.error("Unable to send activation email for user with event {}", eventName);
                        log.error(e.getMessage(), e);
                        throw new UnprocessableEntityException(NotificationErrorCode.SEND_ACTIVATION_EMAIL_FAILED);
                    }
                });
    }
}

