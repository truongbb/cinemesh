package com.cinemesh.bookingservice.application.dto.response;

import com.cinemesh.bookingservice.statics.OrderPaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

    UUID id;
    BigDecimal totalAmount;
    OrderPaymentStatus paymentStatus;
    List<TicketResponse> tickets;

}
