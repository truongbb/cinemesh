package com.cinemesh.paymentservice.event.listener;

import com.cinemesh.paymentservice.event.service.PaymentNotificationEventKafkaService;
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
public class PaymentNotificationListener {

    @Value("${application.kafka.payment-notification.group-id}")
    private String groupId;

    @Value("${application.kafka.payment-notification.ignored}")
    private boolean ignored;

    private final PaymentNotificationEventKafkaService eventKafkaService;

//    private final KafkaMessageLogService kafkaMessageLogService;

    @KafkaListener(
            topics = "${application.kafka.payment-notification.topic}",
            groupId = "${application.kafka.payment-notification.group-id}",
            containerFactory = "paymentNotificationKafkaListenerContainerFactory",
            autoStartup = "${application.kafka.payment-notification.auto-startup}"
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
