package com.cinemesh.notificationservice.event.listener;

import com.cinemesh.notificationservice.event.service.OrderLogEventKafkaService;
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
public class OrderLogListener {

    @Value("${application.kafka.order-log.group-id}")
    private String groupId;

    @Value("${application.kafka.order-log.ignored}")
    private boolean ignored;

    private final OrderLogEventKafkaService eventKafkaService;

//    private final KafkaMessageLogService kafkaMessageLogService;

    @KafkaListener(
            topics = "${application.kafka.order-log.topic}",
            groupId = "${application.kafka.order-log.group-id}",
            containerFactory = "orderLogKafkaListenerContainerFactory",
            autoStartup = "${application.kafka.order-log.auto-startup}"
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
