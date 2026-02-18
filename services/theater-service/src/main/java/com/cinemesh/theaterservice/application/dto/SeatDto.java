package com.cinemesh.theaterservice.application.dto;

import com.cinemesh.theaterservice.statics.SeatType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatDto {

    UUID id;
    SeatType seatType;
    String rowCode;
    Integer columnNumber;
    SeatType type;

}
