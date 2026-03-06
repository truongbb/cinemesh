package com.cinemesh.paymentservice.application.dto;

import com.cinemesh.paymentservice.statics.PaymentNotificationStatus;
import com.cinemesh.common.statics.PaymentPartner;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentNotificationDto {

    UUID id;
    PaymentPartner paymentPartner;
    String rawPayload;
    PaymentNotificationStatus status;
    String errorLog;

}
