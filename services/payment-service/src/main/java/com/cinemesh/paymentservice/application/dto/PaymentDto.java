package com.cinemesh.paymentservice.application.dto;

import com.cinemesh.common.statics.PaymentCurrency;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.common.statics.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {

    UUID id;
    UUID orderId;
    BigDecimal amount;
    BigDecimal paidAmount;
    PaymentCurrency currency;
    PaymentPartner paymentPartner;
    String transactionId;
    PaymentStatus status;


}
