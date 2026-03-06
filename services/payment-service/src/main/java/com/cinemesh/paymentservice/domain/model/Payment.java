package com.cinemesh.paymentservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.event.domain.CinemeshEvent;
import com.cinemesh.common.event.domain.CinemeshEventName;
import com.cinemesh.common.event.domain.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.paymentservice.application.dto.PaymentDto;
import com.cinemesh.common.statics.PaymentCurrency;
import com.cinemesh.common.statics.PaymentPartner;
import com.cinemesh.common.statics.PaymentStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Payment extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private UUID orderId;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private PaymentCurrency currency;
    private PaymentPartner paymentPartner;
    private String transactionId;
    private PaymentStatus status;


    public Payment() {
        this.id = UUID.randomUUID();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.PAYMENT_CREATED, id));
    }

    public Payment(PaymentDto dto) {
        this.id = dto.getId() == null ? UUID.randomUUID() : dto.getId();
        this.orderId = dto.getOrderId();
        this.amount = dto.getAmount();
        this.paidAmount = dto.getPaidAmount();
        this.currency = dto.getCurrency();
        this.paymentPartner = dto.getPaymentPartner();
        this.transactionId = dto.getTransactionId();
        this.status = dto.getStatus();
        create();
    }

    public void update(PaymentDto dto) {
        this.id = dto.getId();
        setOrderId(dto.getOrderId());
        setAmount(dto.getAmount());
        setPaidAmount(dto.getPaidAmount());
        setCurrency(dto.getCurrency());
        setPaymentPartner(dto.getPaymentPartner());
        setTransactionId(dto.getTransactionId());
        setStatus(dto.getStatus());
        modify();
    }

    public void setOrderId(UUID orderId) {
        if (ObjectUtils.equals(this.orderId, orderId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("orderId", this.orderId, orderId)));
        this.orderId = orderId;
        modify();
    }

    public void setAmount(BigDecimal amount) {
        if (ObjectUtils.equals(this.amount, amount)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("amount", this.amount, amount)));
        this.amount = amount;
        modify();
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        if (ObjectUtils.equals(this.paidAmount, paidAmount)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("paidAmount", this.paidAmount, paidAmount)));
        this.paidAmount = paidAmount;
        modify();
    }

    public void setCurrency(PaymentCurrency currency) {
        if (ObjectUtils.equals(this.currency, currency)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("currency", this.currency, currency)));
        this.currency = currency;
        modify();
    }

    public void setPaymentPartner(PaymentPartner paymentPartner) {
        if (ObjectUtils.equals(this.paymentPartner, paymentPartner)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("paymentPartner", this.paymentPartner, paymentPartner)));
        this.paymentPartner = paymentPartner;
        modify();
    }

    public void setTransactionId(String transactionId) {
        if (ObjectUtils.equals(this.transactionId, transactionId)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("transactionId", this.transactionId, transactionId)));
        this.transactionId = transactionId;
        modify();
    }

    public void setStatus(PaymentStatus status) {
        if (ObjectUtils.equals(this.status, status)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("status", this.status, status)));
        this.status = status;
        modify();
    }


}
