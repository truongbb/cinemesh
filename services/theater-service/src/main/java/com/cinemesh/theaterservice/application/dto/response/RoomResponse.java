package com.cinemesh.theaterservice.application.dto.response;

import com.cinemesh.theaterservice.statics.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {

    UUID id;
    String name;
    Integer totalSeats;
    RoomStatus status;
    List<SeatResponse> seats;

}
