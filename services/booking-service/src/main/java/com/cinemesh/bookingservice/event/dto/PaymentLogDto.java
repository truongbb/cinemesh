package com.cinemesh.bookingservice.event.dto;

import com.cinemesh.common.statics.LogType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentLogDto {
    private UUID paymentId;
    private LogType type;
    private String detail;


}
