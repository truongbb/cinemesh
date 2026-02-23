package com.cinemesh.theaterservice.application.service;

import com.cinemesh.common.exception.NotFoundException;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.MovieStatus;
import com.cinemesh.theaterservice.application.dto.ShowtimeDto;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeCreationRequest;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeStatusUpdateRequest;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeUpdateRequest;
import com.cinemesh.theaterservice.application.dto.response.MovieResponse;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.domain.exception.TheaterErrorCode;
import com.cinemesh.theaterservice.domain.model.Room;
import com.cinemesh.theaterservice.domain.model.ShowTime;
import com.cinemesh.theaterservice.infrastructure.feign.MovieFeignClient;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.RoomPersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.adapter.ShowtimePersistenceAdapter;
import com.cinemesh.theaterservice.infrastructure.persistence.mapper.ShowtimeMapper;
import com.cinemesh.theaterservice.statics.ShowtimeStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

        validateShowtimeRequest(movie, request.getStartTime(), request.getEndTime(), null);

        ShowtimeDto showtimeDto = objectMapper.convertValue(request, ShowtimeDto.class);
        showtimeDto.setStatus(ShowtimeStatus.CREATED);
        ShowTime showTime = new ShowTime(showtimeDto);
        showtimePersistenceAdapter.saveShowTime(showTime);

        return showtimeMapper.convertDomainToResponse(showTime, movie, room);
    }

    public ShowtimeResponse updateShowtime(UUID id, @Valid ShowtimeUpdateRequest request) {
        ShowTime showTime = showtimePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.SHOWTIME_NOT_FOUND));

        Room room = roomPersistenceAdapter.findById(request.getRoomId())
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));
        MovieResponse movie = movieFeignClient.getMovieDetails(request.getMovieId());

        validateShowtimeRequest(movie, request.getStartTime(), request.getEndTime(), id);

        ShowtimeDto showtimeDto = objectMapper.convertValue(request, ShowtimeDto.class);
        showtimeDto.setId(id);
        showtimeDto.setStatus(showTime.getStatus());
        showTime.update(showtimeDto);
        showtimePersistenceAdapter.saveShowTime(showTime);

        return showtimeMapper.convertDomainToResponse(showTime, movie, room);
    }

    private void validateShowtimeRequest(MovieResponse movie, LocalDateTime startTime, LocalDateTime endTime, UUID showtimeId) {
        // check xem phòng chiếu đó tại khung giờ đó có free hay không
        List<ShowTime> timeIntervalOverlapping =
                showtimePersistenceAdapter.findTimeIntervalOverlapping(startTime, endTime,
                        showtimeId == null ? Collections.emptyList() : Collections.singletonList(showtimeId));
        if (!CollectionUtils.isEmpty(timeIntervalOverlapping)) {
            throw new UnprocessableEntityException(TheaterErrorCode.ROOM_OCCUPIED);
        }

        if (movie == null) {
            throw new NotFoundException(TheaterErrorCode.MOVIE_NOT_FOUND);
        }
        if (!movie.getStatus().equals(MovieStatus.NOW_SHOWING)) { // phim có đang được cho phép chiếu không
            throw new UnprocessableEntityException(TheaterErrorCode.MOVIE_IS_NOT_PUBLISHED_TO_SHOW);
        }

        if (startTime.isAfter(endTime)) {
            throw new UnprocessableEntityException(TheaterErrorCode.SHOWTIME_START_TIME_MUST_GREATER_THAN_END_TIME);
        }
    }

    public ShowtimeResponse updateShowtimeStatus(UUID id, @Valid ShowtimeStatusUpdateRequest request) {
        ShowTime showTime = showtimePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.SHOWTIME_NOT_FOUND));

        Room room = roomPersistenceAdapter.findById(showTime.getRoomId())
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));
        MovieResponse movie = movieFeignClient.getMovieDetails(showTime.getMovieId());

        if (showTime.getStatus().equals(ShowtimeStatus.SHOWING) && request.getStatus().equals(ShowtimeStatus.CREATED)) {
            throw new UnprocessableEntityException(TheaterErrorCode.INVALID_SHOWTIME_STATUS_PROCESS);
        }

        if (
                showTime.getStatus().equals(ShowtimeStatus.SHOWN)
                        && (request.getStatus().equals(ShowtimeStatus.CREATED) || request.getStatus().equals(ShowtimeStatus.SHOWING))
        ) {
            throw new UnprocessableEntityException(TheaterErrorCode.INVALID_SHOWTIME_STATUS_PROCESS);
        }

        showTime.setStatus(request.getStatus());
        showtimePersistenceAdapter.saveShowTime(showTime);

        return showtimeMapper.convertDomainToResponse(showTime, movie, room);
    }

    public ShowtimeResponse getDetails(UUID id) {
        ShowTime showTime = showtimePersistenceAdapter.findById(id)
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.SHOWTIME_NOT_FOUND));

        Room room = roomPersistenceAdapter.findById(showTime.getRoomId())
                .orElseThrow(() -> new NotFoundException(TheaterErrorCode.ROOM_NOT_FOUND));
        MovieResponse movie = movieFeignClient.getMovieDetails(showTime.getMovieId());

        return showtimeMapper.convertDomainToResponse(showTime, movie, room);
    }

}
