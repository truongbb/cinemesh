package com.cinemesh.bookingservice.application.dto;

import com.cinemesh.common.statics.TicketStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketDto {

    UUID id;
    UUID showtimeId;
    UUID seatId;
    BigDecimal price;
    TicketStatus status;

}
