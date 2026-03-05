package com.cinemesh.paymentservice.infrastructure.feign.response;

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
public class TicketResponse {

    UUID id;
    ShowtimeResponse showtime;
    SeatResponse seat;
    BigDecimal price;
    TicketStatus status;

}
