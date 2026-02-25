package com.cinemesh.theaterservice.application.dto;

import com.cinemesh.common.statics.SeatType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDto {

    UUID id;
    String rowCode;
    Integer columnNumber;
    SeatType type;

}
