package com.cinemesh.theaterservice.application.service;

import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.theaterservice.application.dto.ShowtimeDto;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeCreationRequest;
import com.cinemesh.theaterservice.application.dto.response.MovieResponse;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.domain.model.ShowTime;
import com.cinemesh.theaterservice.infrastructure.feign.MovieFeignClient;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.ShowtimePersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.ShowtimeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeService {

    ObjectMapper objectMapper;
    ShowtimeMapper showtimeMapper;
    MovieFeignClient movieFeignClient;
    RoomPersistenceAdapter roomPersistenceAdapter;
    ShowtimePersistenceAdapter showtimePersistenceAdapter;

    public ShowtimeResponse createShowtime(@Valid ShowtimeCreationRequest request) {
        Room room = roomPersistenceAdapter.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));

        MovieResponse movie = movieFeignClient.getMovieDetails(request.getMovieId());
        if (movie == null) {
            throw new NotFoundException(TheaterErrorCode.MOVIE_NOT_FOUND);
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new UnprocessableEntityException(TheaterErrorCode.SHOWTIME_START_TIME_MUST_GREATER_THAN_END_TIME);
        }

        ShowtimeDto showtimeDto = objectMapper.convertValue(request, ShowtimeDto.class);
        ShowTime showTime = new ShowTime(showtimeDto);
        showtimePersistenceAdapter.saveShowTime(showTime);

        return showtimeMapper.convertDomainToResponse(showTime, movie, room);
    }

}
