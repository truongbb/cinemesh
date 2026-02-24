package com.cinemesh.theaterservice.application.dto.request;

import com.cinemesh.common.dto.request.CommonSearchRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShowtimeSearchRequest extends CommonSearchRequest {

    private UUID movieId;

    private LocalDate showingDate;


}
