package com.cinemesh.notificationservice.event.dto;

import com.cinemesh.common.statics.LogType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserLogDto {
    private UUID userId;
    private LogType type;
    private String detail;


}
