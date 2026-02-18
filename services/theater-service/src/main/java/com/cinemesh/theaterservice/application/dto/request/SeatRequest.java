package com.cinemesh.theaterservice.application.dto.request;


import com.cinemesh.theaterservice.statics.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SeatRequest {

    UUID id;

    @NotBlank(message = "Seat row code is required")
    String rowCode;

    @NotNull(message = "Seat column number required")
    @Positive(message = "Seat column number must be positive")
    Integer columnNumber;

    @NotNull(message = "Seat type required")
    SeatType type;

}
