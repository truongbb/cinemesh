package com.cinemesh.theaterservice.domain.repository;

import com.cinemesh.theaterservice.domain.model.Seat;
import com.cinemesh.theaterservice.statics.SeatType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepository {

    Optional<Seat> findById(UUID id);

    List<Seat> findAllByIds(List<UUID> ids);

    List<Seat> findByType(SeatType type);

    Seat findByRowCodeAndColumnNumber(String rowCode, int columnNumber);

    void saveSeat(Seat seat);

}
