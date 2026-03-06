package com.cinemesh.paymentservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.paymentservice.statics.PaymentNotificationStatus;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "TEXT")
    private String rawPayload;

    @Enumerated(EnumType.STRING)
    private PaymentNotificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String errorLog;

}
