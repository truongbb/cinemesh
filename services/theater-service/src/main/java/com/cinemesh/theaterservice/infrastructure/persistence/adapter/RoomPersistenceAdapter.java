package com.cinemesh.theaterservice.infrastructure.persistence.adapter;

import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomEntity;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.RoomLogEntity;
import com.cinemesh.theaterservice.infrastructure.persistence.repository.RoomLogRepository;
import com.cinemesh.theaterservice.infrastructure.persistence.repository.RoomRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomPersistenceAdapter implements com.cinemesh.theaterservice.domain.repository.RoomRepository {

    ObjectMapper objectMapper;
    RoomRepository roomRepository;
    RoomLogRepository roomLogRepository;


    @Override
    public Optional<Room> findById(UUID id) {
        return roomRepository.findById(id)
                .map(room -> objectMapper.convertValue(room, Room.class));
    }

    @Override
    public List<Room> findAllByIds(List<UUID> ids) {
        return roomRepository.findAllById(ids)
                .stream()
                .map(room -> objectMapper.convertValue(room, Room.class))
                .toList();
    }

    @Override
    public Optional<Room> findByName(String name) {
        return roomRepository.findByName(name)
                .map(room -> objectMapper.convertValue(room, Room.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoom(Room room) {
        try {
            RoomLogEntity log = new RoomLogEntity();
            log.setId(UUID.randomUUID());
            log.setRoomId(room.getId());
            log.setType(LogType.getByIsCreated(room.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(room));

            RoomEntity entity = objectMapper.convertValue(room, RoomEntity.class);
            entity.getSeats().forEach(seat -> seat.setRoom(entity)); // to save room_id in `seats` table
            roomRepository.save(entity);
            roomLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }
}
