package com.cinemesh.theaterservice.domain.repository;

import com.cinemesh.theaterservice.domain.model.ShowTime;

import java.util.Optional;
import java.util.UUID;

public interface ShowtimeRepository {

    Optional<ShowTime> findById(UUID id);

    void saveShowTime(ShowTime showTime);

}
