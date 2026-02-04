package com.cinemesh.common.dto.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DomainEventDto implements Serializable {
    private String name;
    private Object payload;
    private Instant createdAt;
}
