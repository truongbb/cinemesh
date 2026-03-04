package com.cinemesh.paymentservice.application.dto;

import com.cinemesh.paymentservice.statics.PaymentCurrency;
import com.cinemesh.paymentservice.statics.PaymentPartner;
import com.cinemesh.paymentservice.statics.PaymentStatus;
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
