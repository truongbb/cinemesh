package com.cinemesh.theaterservice.domain.repository;

import com.cinemesh.theaterservice.domain.model.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository {

    Optional<Room> findById(UUID id);

    List<Room> findAllByIds(List<UUID> ids);

//    Optional<Room> findByName(String name);

    void saveRoom(Room room);

}
