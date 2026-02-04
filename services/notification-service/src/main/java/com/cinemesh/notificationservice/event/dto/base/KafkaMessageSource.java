package com.cinemesh.notificationservice.event.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaMessageSource {
    String version;
    String connector;
    String name;
    @JsonProperty("ts_ms")
    String tsMs;
    String snapshot;
    String db;
    String schema;
    String table;
    String txId;
    String lsn;
    String xmin;
}
