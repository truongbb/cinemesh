package com.cinemesh.bookingservice.application.dto;

import com.cinemesh.common.statics.OrderPaymentStatus;
import com.cinemesh.common.statics.OrderStatus;
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
public class OrderDto {

    UUID id;
    UUID userId;
    BigDecimal totalAmount;
    OrderPaymentStatus paymentStatus;
    OrderStatus status;
    List<TicketDto> tickets;

}
