package com.cinemesh.bookingservice.application.dto;

import com.cinemesh.bookingservice.statics.OrderPaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {

    UUID id;
    UUID userId;
    BigDecimal totalAmount;
    OrderPaymentStatus paymentStatus;
    List<TicketDto> tickets;

}
