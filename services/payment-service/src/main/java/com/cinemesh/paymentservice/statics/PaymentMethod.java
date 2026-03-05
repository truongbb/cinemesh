package com.cinemesh.paymentservice.statics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    VNPAY_QR("VNPAYQR"),
    VNPAY_ATM("VNBANK");

    private final String vnPayValue;


}
