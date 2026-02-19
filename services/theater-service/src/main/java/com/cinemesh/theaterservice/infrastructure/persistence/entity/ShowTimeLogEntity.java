package com.cinemesh.theaterservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.LogType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "show_time_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowTimeLogEntity extends BaseEntity {

    @Column
    private UUID showtimeId;

    @Column
    @Enumerated(EnumType.STRING)
    private LogType type;

    @Column(columnDefinition = "TEXT")
    private String detail;


}
