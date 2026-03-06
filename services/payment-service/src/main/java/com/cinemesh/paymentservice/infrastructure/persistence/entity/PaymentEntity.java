package com.cinemesh.paymentservice.infrastructure.persistence.entity;

import com.cinemesh.common.infrastructure.persistence.entity.BaseEntity;
import com.cinemesh.common.statics.PaymentCurrency;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.common.statics.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity extends BaseEntity {

    private UUID orderId;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    private PaymentCurrency currency;

    @Enumerated(EnumType.STRING)
    private PaymentPartner paymentPartner;

    private String transactionId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

}
