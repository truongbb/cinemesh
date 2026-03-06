package com.cinemesh.paymentservice.application.dto.request;

import com.cinemesh.paymentservice.statics.PaymentMethod;
import com.cinemesh.common.statics.PaymentPartner;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequestPayload {
    @NotNull(message = "Order id required")
    private UUID orderId;

    @NotNull(message = "Payment method required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment partner required")
    private PaymentPartner paymentPartner;
}
