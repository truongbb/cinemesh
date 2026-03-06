package com.cinemesh.common.event.kafka.dto;

import lombok.Data;

@Data
public class KafkaMessageDto<T> {

    private KafkaMessagePayloadDto<T> payload;

}
