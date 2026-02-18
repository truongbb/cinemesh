package com.cinemesh.theaterservice.infrastructure.persistence.mapper;

import com.cinemesh.theaterservice.application.dto.RoomDto;
import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.application.dto.request.RoomCreationRequest;
import com.cinemesh.theaterservice.application.dto.request.SeatRequest;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.application.dto.response.SeatResponse;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.statics.RoomStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomMapper {

    ObjectMapper objectMapper;

    public RoomDto convertFromRequestToDto(RoomCreationRequest roomCreationRequest) {
        List<SeatRequest> seats = CollectionUtils.isEmpty(roomCreationRequest.getSeats()) ? new ArrayList<>() : roomCreationRequest.getSeats();
        return RoomDto.builder()
                .name(roomCreationRequest.getName())
                .seats(
                        seats.stream()
                                .map(seatRequest -> objectMapper.convertValue(seatRequest, SeatDto.class))
                                .toList()
                )
                .status(RoomStatus.CREATED)
                .totalSeats(seats.size())
                .build();
    }

    public RoomResponse convertDomainToResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .status(room.getStatus())
                .totalSeats(room.getTotalSeats())
                .seats(room.getSeats().stream().map(seat -> objectMapper.convertValue(seat, SeatResponse.class)).toList())
                .build();
    }

}
