package com.cinemesh.theaterservice.infrastructure.persistence.mapper;

import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.application.dto.request.SeatRequest;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.application.dto.response.SeatResponse;
import com.cinemesh.theaterservice.domain.model.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatMapper {

    ObjectMapper objectMapper;

    public SeatDto convertFromRequestToDto(SeatRequest request) {
        return objectMapper.convertValue(request, SeatDto.class);
    }

}
