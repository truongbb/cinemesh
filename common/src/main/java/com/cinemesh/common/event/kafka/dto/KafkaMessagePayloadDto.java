package com.cinemesh.common.event.kafka.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaMessagePayloadDto<T> extends EventMessage {

    private T after;

}
