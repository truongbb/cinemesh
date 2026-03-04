package com.cinemesh.paymentservice.domain.model;

import com.cinemesh.common.domain.AggregateRoot;
import com.cinemesh.common.domain.BaseEntity;
import com.cinemesh.common.event.CinemeshEvent;
import com.cinemesh.common.event.CinemeshEventName;
import com.cinemesh.common.event.payload.FieldChangedPayload;
import com.cinemesh.common.utils.ObjectUtils;
import com.cinemesh.paymentservice.application.dto.PaymentNotificationDto;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentNotification extends BaseEntity<UUID> implements AggregateRoot<UUID> {

    private String paymentPartner;
    private String rawPayload;

    public PaymentNotification() {
        this.id = UUID.randomUUID();
        create();
        addEvent(new CinemeshEvent(CinemeshEventName.PAYMENT_NOTIFICATION_CREATED, this.id));
    }

    public PaymentNotification(PaymentNotificationDto dto) {
        this.id = UUID.randomUUID();
        this.paymentPartner = dto.getPaymentPartner();
        this.rawPayload = dto.getRawPayload();
        create();
    }

    public void update(PaymentNotificationDto dto) {
        setPaymentPartner(dto.getPaymentPartner());
        setRawPayload(dto.getRawPayload());
        modify();
    }

    public void setPaymentPartner(String paymentPartner) {
        if (ObjectUtils.equals(this.paymentPartner, paymentPartner)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("paymentPartner", this.paymentPartner, paymentPartner)));
        this.paymentPartner = paymentPartner;
        modify();
    }

    public void setRawPayload(String rawPayload) {
        if (ObjectUtils.equals(this.rawPayload, rawPayload)) return;
        addEvent(new CinemeshEvent(CinemeshEventName.FIELD_VALUE_CHANGED, new FieldChangedPayload("rawPayload", this.rawPayload, rawPayload)));
        this.rawPayload = rawPayload;
        modify();
    }

}
