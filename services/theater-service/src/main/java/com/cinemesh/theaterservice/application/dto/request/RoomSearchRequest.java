package com.cinemesh.theaterservice.application.dto.request;

import com.cinemesh.common.dto.request.CommonSearchRequest;
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
public class RoomSearchRequest extends CommonSearchRequest {

    String name;

    RoomStatus status;

}
