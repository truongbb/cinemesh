package com.cinemesh.theaterservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.theaterservice.statics.RoomStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity extends BaseEntity {

    @Column
    private String name;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @Column
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "room_seats",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id")
    )
    private List<SeatEntity> seats;

}
