package com.cinemesh.theaterservice.application.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeDto {

    UUID id;
    UUID movieId;
    UUID roomId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    BigDecimal basePrice;

}
