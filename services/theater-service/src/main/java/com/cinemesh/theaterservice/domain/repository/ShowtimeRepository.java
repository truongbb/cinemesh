package com.cinemesh.theaterservice.domain.repository;

import com.cinemesh.theaterservice.domain.model.ShowTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShowtimeRepository {

    Optional<ShowTime> findById(UUID id);

    void saveShowTime(ShowTime showTime);

    List<ShowTime> findTimeIntervalOverlapping(LocalDateTime start, LocalDateTime end, List<UUID> excludedIds);

}
