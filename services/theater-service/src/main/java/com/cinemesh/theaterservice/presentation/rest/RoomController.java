package com.cinemesh.theaterservice.presentation.rest;

import com.cinemesh.common.dto.response.CommonSearchResponse;
import com.cinemesh.theaterservice.application.dto.request.RoomCreationRequest;
import com.cinemesh.theaterservice.application.dto.request.RoomSearchRequest;
import com.cinemesh.theaterservice.application.dto.request.RoomStatusUpdateRequest;
import com.cinemesh.theaterservice.application.dto.request.RoomUpdateRequest;
import com.cinemesh.theaterservice.application.dto.response.RoomResponse;
import com.cinemesh.theaterservice.application.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {

    RoomService roomService;

    @PostMapping
    public RoomResponse createRoom(@RequestBody @Valid RoomCreationRequest request) {
        return roomService.createRoom(request);
    }

    @PutMapping("/{id}")
    public RoomResponse updateRoom(@RequestBody @Valid RoomUpdateRequest request, @NotNull @PathVariable UUID id) {
        return roomService.updateRoom(id, request);
    }

    @PutMapping("/{id}/status")
    public RoomResponse updateRoomStatus(@RequestBody @Valid RoomStatusUpdateRequest request, @NotNull @PathVariable UUID id) {
        return roomService.updateRoomStatus(id, request);
    }

    @GetMapping("/{id}")
    public RoomResponse getRoomDetails(@NotNull @PathVariable UUID id) {
        return roomService.getRoomDetails(id);
    }

    @GetMapping
    public CommonSearchResponse<RoomResponse> searchRoom(RoomSearchRequest request) {
        return roomService.searchRoom(request);
    }

}
