package com.cinemesh.common.dto.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseDomainEntityDto<TId> {
    TId id;
    List<DomainEventDto> events;
    Integer version;
    String createdBy;
    Instant createdAt;
    Instant modifiedAt;
    boolean created;
    boolean modified;

}
