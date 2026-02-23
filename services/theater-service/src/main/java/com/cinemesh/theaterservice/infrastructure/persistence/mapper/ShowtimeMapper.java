package com.cinemesh.theaterservice.infrastructure.persistence.mapper;

import com.cinemesh.theaterservice.application.dto.response.MovieResponse;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.domain.model.ShowTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeMapper {

    ObjectMapper objectMapper;
    RoomMapper roomMapper;

    public ShowtimeResponse convertDomainToResponse(ShowTime showTime, MovieResponse movieResponse, Room room) {
        return ShowtimeResponse.builder()
                .id(showTime.getId())
                .movie(movieResponse)
                .room(roomMapper.convertDomainToResponse(room))
                .startTime(showTime.getStartTime())
                .endTime(showTime.getEndTime())
                .basePrice(showTime.getBasePrice())
                .status(showTime.getStatus())
                .build();
    }

}
