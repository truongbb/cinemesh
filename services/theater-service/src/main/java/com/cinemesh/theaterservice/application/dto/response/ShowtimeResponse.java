package com.cinemesh.theaterservice.application.dto.response;

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
public class ShowtimeResponse {

    private UUID id;
    private MovieResponse movie;
    private RoomResponse room;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal basePrice;

}
