package com.cinemesh.theaterservice.application.service;

import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.application.dto.request.RoomCreationRequest;
import com.cinemesh.theaterservice.application.dto.request.RoomUpdateRequest;
import com.cinemesh.theaterservice.application.dto.request.SeatRequest;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.SeatPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.RoomMapper;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.SeatMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomMapper roomMapper;
    SeatMapper seatMapper;
    RoomPersistenceAdapter roomPersistenceAdapter;
    SeatPersistenceAdapter seatPersistenceAdapter;

    public RoomResponse createRoom(@Valid RoomCreationRequest request) {
        roomPersistenceAdapter.findByName(request.getName())
                .ifPresent(room -> {
                    throw new UnprocessableEntityException(TheaterErrorCode.ROOM_EXISTED);
                });

        Room room = new Room(roomMapper.convertFromRequestToDto(request));
        roomPersistenceAdapter.saveRoom(room);
        return roomMapper.convertDomainToResponse(room);
    }

    public RoomResponse updateRoom(UUID id, @Valid RoomUpdateRequest request) {
        Room room = roomPersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));

        roomPersistenceAdapter.findByName(request.getName())
                .ifPresent(room1 -> {
                    if (!room1.getId().equals(id)) { // nếu có room nào trùng tên mà không phải id muốn cập nhật -> bắn
                        throw new UnprocessableEntityException(TheaterErrorCode.ROOM_EXISTED);
                    }
                });

        List<SeatRequest> seatRequests = CollectionUtils.isEmpty(request.getSeats()) ? new ArrayList<>() : request.getSeats();
        List<SeatDto> seatDtos = seatRequests.stream().map(seatMapper::convertFromRequestToDto).toList();
        room.updateSeats(seatDtos);

        room.setName(request.getName());
        room.setStatus(request.getStatus());
        room.setTotalSeats(seatDtos.size());

        roomPersistenceAdapter.saveRoom(room);
        return roomMapper.convertDomainToResponse(room);
    }
}
