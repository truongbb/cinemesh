package com.cinemesh.theaterservice.application.dto.response;


import com.cinemesh.theaterservice.statics.SeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatResponse {

    UUID id;
    String rowCode;
    Integer columnNumber;
    SeatType type;

}
