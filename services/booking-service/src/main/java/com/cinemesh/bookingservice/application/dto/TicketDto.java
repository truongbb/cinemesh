package com.cinemesh.bookingservice.application.dto;

import com.cinemesh.bookingservice.statics.TicketStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
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
