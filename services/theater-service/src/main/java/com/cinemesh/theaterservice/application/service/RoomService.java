package com.cinemesh.theaterservice.application.service;

import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.theaterservice.application.dto.request.RoomRequest;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.SeatPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.RoomMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomMapper roomMapper;
    RoomPersistenceAdapter roomPersistenceAdapter;
    SeatPersistenceAdapter seatPersistenceAdapter;

    public RoomResponse createRoom(@Valid RoomRequest request) {
        roomPersistenceAdapter.findByName(request.getName())
                .ifPresent(room -> {
                    throw new UnprocessableEntityException(TheaterErrorCode.ROOM_EXISTED);
                });

        Room room = new Room(roomMapper.convertFromRequestToDto(request));
        roomPersistenceAdapter.saveRoom(room);

        return roomMapper.convertDomainToResponse(room);
    }

}
