package com.cinemesh.bookingservice.infrastructure.feign.response;

import com.cinemesh.common.statics.RoomStatus;
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
