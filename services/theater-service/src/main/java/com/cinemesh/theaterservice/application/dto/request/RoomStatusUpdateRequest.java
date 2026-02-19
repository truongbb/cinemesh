package com.cinemesh.theaterservice.application.dto.request;

import com.cinemesh.theaterservice.statics.RoomStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomStatusUpdateRequest {

    RoomStatus status;

}
