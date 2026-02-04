package com.cinemesh.movieservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.LogType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "movie_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieLogEntity extends BaseEntity {

    @Column
    private UUID movieId;

    @Column
    private LogType type;

    @Column(columnDefinition = "TEXT")
    private String detail;


}
