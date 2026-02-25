package com.cinemesh.bookingservice.infrastructure.feign.response;


import com.cinemesh.common.statics.SeatType;
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
