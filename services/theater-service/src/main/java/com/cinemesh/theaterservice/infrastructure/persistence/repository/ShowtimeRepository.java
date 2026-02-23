package com.cinemesh.theaterservice.infrastructure.persistence.repository;

import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ShowtimeRepository extends JpaRepository<ShowTimeEntity, UUID>, JpaSpecificationExecutor<ShowTimeEntity> {

    @Query("select s from ShowTimeEntity s " +
            "where (:startShow between s.startTime and s.endTime " +
            "or :endShow between s.startTime and s.endTime) and s.id not in :excludeIds")
    List<ShowTimeEntity> findTimeIntervalOverlapping(LocalDateTime startShow, LocalDateTime endShow, List<UUID> excludeIds);

}
