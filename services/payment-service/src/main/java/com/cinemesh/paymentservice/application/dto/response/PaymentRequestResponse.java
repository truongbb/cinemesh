package com.cinemesh.paymentservice.application.dto.response;


import com.cinemesh.common.statics.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentRequestResponse {
    private UUID paymentId;
    private String paymentUrl;
    private PaymentStatus status;
}
