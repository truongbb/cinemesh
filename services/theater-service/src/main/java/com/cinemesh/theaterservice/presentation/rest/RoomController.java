package com.cinemesh.theaterservice.presentation.rest;

import com.cinemesh.theaterservice.application.dto.request.RoomRequest;
import com.cinemesh.theaterservice.application.service.RoomService;
import com.cinemesh.theaterservice.domain.model.Room;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {

    RoomService roomService;

    @PostMapping
    public Room createRoom(@RequestBody @Valid RoomRequest request) {
        return roomService.createRoom(request);
    }

}
