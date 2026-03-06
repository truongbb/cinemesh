package com.cinemesh.paymentservice.infrastructure.feign.response;

import com.cinemesh.common.statics.OrderStatus;
import com.cinemesh.common.statics.PaymentStatus;
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
    PaymentStatus paymentStatus;
    List<TicketResponse> tickets;
    OrderStatus status;

}
