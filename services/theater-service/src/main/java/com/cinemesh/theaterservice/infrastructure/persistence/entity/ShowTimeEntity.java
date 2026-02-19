package com.cinemesh.theaterservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "show_times")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeEntity extends BaseEntity {

    private UUID movieId;

    private UUID roomId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal basePrice;

}
