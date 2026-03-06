package com.cinemesh.common.event.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public class EventMessage {
    Object before;
    KafkaMessageSource source;
    String op;
    @JsonProperty("ts_ms")
    String tsMs;
    String transaction;
}
