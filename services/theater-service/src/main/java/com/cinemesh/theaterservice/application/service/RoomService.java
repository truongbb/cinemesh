package com.cinemesh.theaterservice.application.service;

import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.theaterservice.application.dto.SeatDto;
import com.cinemesh.theaterservice.application.dto.request.*;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.SeatPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomEntity;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.RoomMapper;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.SeatMapper;
import com.cinemesh.theaterservice.infrastructure.persistence.repository.RoomRepository;
import com.cinemesh.theaterservice.infrastructure.persistence.specification.RoomSpecification;
import com.cinemesh.theaterservice.statics.RoomStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomMapper roomMapper;
    SeatMapper seatMapper;
    RoomPersistenceAdapter roomPersistenceAdapter;
    SeatPersistenceAdapter seatPersistenceAdapter;
    RoomRepository roomRepository;

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

    public RoomResponse updateRoomStatus(@NotNull UUID id, @Valid RoomStatusUpdateRequest request) {
        Room room = roomPersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));

        RoomStatus status = room.getStatus();

        RoomStatus requestStatus = request.getStatus();

        if ((Objects.requireNonNull(status) == RoomStatus.ACTIVE || status == RoomStatus.MAINTENANCE)
                && RoomStatus.CREATED.equals(requestStatus)) {
            throw new UnprocessableEntityException(TheaterErrorCode.ROOM_STATUS_CANNOT_BACKWARD_TO_CREATED);
        }

        room.setStatus(requestStatus);
        roomPersistenceAdapter.saveRoom(room);
        return roomMapper.convertDomainToResponse(room);
    }

    public RoomResponse getRoomDetails(@NotNull UUID id) {
        Room room = roomPersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));
        return roomMapper.convertDomainToResponse(room);
    }

    public CommonSearchResponse<RoomResponse> searchRoom(RoomSearchRequest request) {
        PageRequest pageable = PageRequest.of(request.getPageIndex(), request.getPageSize());

        Specification<RoomEntity> spec = RoomSpecification.search(request);

        Page<RoomEntity> pageResult = roomRepository.findAll(spec, pageable);

        return CommonSearchResponse.<RoomResponse>builder()
                .data(
                        pageResult.getContent()
                                .stream()
                                .map(roomMapper::convertEntityToResponse)
                                .toList()
                )
                .pagination(
                        CommonSearchResponse.PaginationResponse.builder()
                                .pageSize(request.getPageSize())
                                .pageIndex(request.getPageIndex())
                                .totalRecords(pageResult.getTotalElements())
                                .totalPage(pageResult.getTotalPages())
                                .build()
                )
                .build();
    }

}
