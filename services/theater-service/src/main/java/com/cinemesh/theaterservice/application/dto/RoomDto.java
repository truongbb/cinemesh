package com.cinemesh.theaterservice.application.dto;

import com.cinemesh.common.statics.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {

    UUID id;
    String name;
    Integer totalSeats;
    RoomStatus status;
    List<SeatDto> seats;

}
