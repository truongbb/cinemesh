package com.cinemesh.paymentservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.paymentservice.statics.PaymentNotificationStatus;
import com.cinemesh.paymentservice.statics.PaymentPartner;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotificationEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private PaymentPartner paymentPartner;

    private String rawPayload;

    @Enumerated(EnumType.STRING)
    private PaymentNotificationStatus status;

    private String errorLog;

}
