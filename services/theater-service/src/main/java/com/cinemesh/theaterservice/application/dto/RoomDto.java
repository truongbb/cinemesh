package com.cinemesh.theaterservice.application.dto;

import com.cinemesh.theaterservice.statics.RoomStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {

    String name;
    Integer totalSeats;
    RoomStatus status;
    List<SeatDto> seats;

}
