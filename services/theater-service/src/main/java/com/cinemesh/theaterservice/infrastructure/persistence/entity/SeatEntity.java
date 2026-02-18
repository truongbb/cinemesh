package com.cinemesh.theaterservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.theaterservice.statics.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    private RoomEntity room;

}
