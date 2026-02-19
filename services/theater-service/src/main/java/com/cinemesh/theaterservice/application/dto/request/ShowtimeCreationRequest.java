package com.cinemesh.theaterservice.application.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeCreationRequest {

    @NotNull(message = "Movie id required")
    private UUID movieId;

    @NotNull(message = "Movie id required")
    private UUID roomId;

    @NotNull(message = "Start time required")
    @Future(message = "Start time must be future time")
    private LocalDateTime startTime;

    @NotNull(message = "End time required")
    @Future(message = "End time must be future time")
    private LocalDateTime endTime;

    @NotNull(message = "Base price required")
    @Positive(message = "Base price must be positive number")
    private BigDecimal basePrice;

}
