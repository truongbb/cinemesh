package com.cinemesh.paymentservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Entity;
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

    private String paymentPartner;

    private String rawPayload;

}
