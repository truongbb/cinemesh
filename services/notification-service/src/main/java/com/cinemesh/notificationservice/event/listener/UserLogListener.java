package com.cinemesh.notificationservice.event.listener;

import com.cinemesh.notificationservice.event.service.UserLogEventKafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLogListener {

    @Value("${application.kafka.user-log.group-id}")
    private String groupId;

    @Value("${application.kafka.user-log.ignored}")
    private boolean ignored;

    private final UserLogEventKafkaService eventKafkaService;

//    private final KafkaMessageLogService kafkaMessageLogService;

    @KafkaListener(
            topics = "${application.kafka.user-log.topic}",
            groupId = "${application.kafka.user-log.group-id}",
            containerFactory = "ulGen4KafkaListenerContainerFactory",
            autoStartup = "${application.kafka.user-log.auto-startup}"
    )
    public void listenRecord(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        try {
            eventKafkaService.handleEvent(record, acknowledgment, groupId, ignored);
        } catch (Exception e) {
//            kafkaMessageLogService.saveKafkaMessage(record, KafkaMessageStatus.FAILED, ExceptionUtils.getStackTrace(e), groupId, ignored);
            log.error(e.getMessage(), e);
        }
    }
}
