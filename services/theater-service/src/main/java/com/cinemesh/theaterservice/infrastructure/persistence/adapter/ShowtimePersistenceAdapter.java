package com.cinemesh.theaterservice.infrastructure.persistence.adapter;

import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.cinemesh.theaterservice.domain.model.ShowTime;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeEntity;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeLogEntity;
import com.cinemesh.theaterservice.infrastructure.persistence.repository.ShowtimeLogRepository;
import com.cinemesh.theaterservice.infrastructure.persistence.repository.ShowtimeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimePersistenceAdapter implements com.cinemesh.theaterservice.domain.repository.ShowtimeRepository {

    ObjectMapper objectMapper;
    ShowtimeRepository showtimeRepository;
    ShowtimeLogRepository showtimeLogRepository;

    @Override
    public Optional<ShowTime> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveShowTime(ShowTime showTime) {
        try {
            ShowTimeLogEntity log = new ShowTimeLogEntity();
            log.setId(UUID.randomUUID());
            log.setShowtimeId(showTime.getId());
            log.setType(LogType.getByIsCreated(showTime.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(showTime));

            ShowTimeEntity entity = objectMapper.convertValue(showTime, ShowTimeEntity.class);
            showtimeRepository.save(entity);
            showtimeLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }

}
