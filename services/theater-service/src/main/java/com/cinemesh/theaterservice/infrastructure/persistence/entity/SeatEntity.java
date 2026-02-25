package com.cinemesh.theaterservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.SeatType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity extends BaseEntity {

    @Column(name = "row_code")
    private String rowCode;

    @Column(name = "column_number")
    private Integer columnNumber;

    @Column
    private SeatType type;

    @ManyToOne
    @JoinColumn(name = "room_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private RoomEntity room;

}
