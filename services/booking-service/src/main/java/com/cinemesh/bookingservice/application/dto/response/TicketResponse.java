package com.cinemesh.bookingservice.application.dto.response;

import com.cinemesh.bookingservice.infrastructure.feign.response.SeatResponse;
import com.cinemesh.bookingservice.infrastructure.feign.response.ShowtimeResponse;
import com.cinemesh.bookingservice.statics.TicketStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TicketResponse {

    UUID id;
    ShowtimeResponse showtime;
    SeatResponse seat;
    BigDecimal price;
    TicketStatus status;

}
