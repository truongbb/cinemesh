package com.cinemesh.bookingservice.event.listener;

import com.cinemesh.bookingservice.event.service.PaymentLogEventKafkaService;
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
public class PaymentLogListener {

    @Value("${application.kafka.payment-log.group-id}")
    private String groupId;

    @Value("${application.kafka.payment-log.ignored}")
    private boolean ignored;

    private final PaymentLogEventKafkaService eventKafkaService;

//    private final KafkaMessageLogService kafkaMessageLogService;

    @KafkaListener(
            topics = "${application.kafka.payment-log.topic}",
            groupId = "${application.kafka.payment-log.group-id}",
            containerFactory = "paymentEventLogKafkaListenerContainerFactory",
            autoStartup = "${application.kafka.payment-log.auto-startup}"
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
