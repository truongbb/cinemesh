package com.cinemesh.theaterservice.infrastructure.persistence.adapter;

import com.cinemesh.theaterservice.domain.model.Seat;
import com.cinemesh.theaterservice.domain.repository.SeatRepository;
import com.cinemesh.theaterservice.statics.SeatType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeatPersistenceAdapter implements SeatRepository {

    @Override
    public Optional<Seat> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Seat> findAllByIds(List<UUID> ids) {
        return List.of();
    }

    @Override
    public List<Seat> findByType(SeatType type) {
        return List.of();
    }

    @Override
    public Seat findByRowCodeAndColumnNumber(String rowCode, int columnNumber) {
        return null;
    }

    @Override
    public void saveSeat(Seat seat) {

    }
}
