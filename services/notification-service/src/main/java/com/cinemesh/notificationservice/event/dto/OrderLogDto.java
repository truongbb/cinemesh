package com.cinemesh.notificationservice.event.dto;

import com.cinemesh.common.statics.LogType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrderLogDto {
    private UUID orderId;
    private LogType type;
    private String detail;


}
