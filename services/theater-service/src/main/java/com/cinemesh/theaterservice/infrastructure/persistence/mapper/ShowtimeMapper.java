package com.cinemesh.theaterservice.infrastructure.persistence.mapper;

import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.theaterservice.application.dto.response.MovieResponse;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.domain.model.ShowTime;
import com.cinemesh.theaterservice.infrastructure.persistence.entity.ShowTimeEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public ShowtimeResponse convertEntityToResponse(ShowTimeEntity showTime, List<Room> rooms, List<MovieResponse> movieResponses) {
        Room room = rooms.stream()
                .filter(r -> r.getId().equals(showTime.getRoomId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));

        MovieResponse movieResponse = movieResponses.stream()
                .filter(m -> m.getId().equals(showTime.getMovieId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.MOVIE_NOT_FOUND));
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
