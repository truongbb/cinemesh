package com.cinemesh.theaterservice.application.service;

import com.cinemesh.theaterservice.application.dto.request.RoomRequest;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.SeatPersistenceAdapter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomPersistenceAdapter roomPersistenceAdapter;
    SeatPersistenceAdapter seatPersistenceAdapter;

    public Room createRoom(@Valid RoomRequest request) {
        return null;
    }
}
