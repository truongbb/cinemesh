package com.cinemesh.theaterservice.presentation.rest;


import com.cinemesh.theaterservice.application.dto.request.ShowtimeCreationRequest;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeStatusUpdateRequest;
import com.cinemesh.theaterservice.application.dto.request.ShowtimeUpdateRequest;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.application.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/showtimes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShowtimeController {

    ShowtimeService showtimeService;

    @PostMapping
    public ShowtimeResponse createShowtime(@RequestBody @Valid ShowtimeCreationRequest request) {
        return showtimeService.createShowtime(request);
    }

    @PutMapping("/{id}")
    public ShowtimeResponse updateShowtime(@RequestBody @Valid ShowtimeUpdateRequest request, @PathVariable UUID id) {
        return showtimeService.updateShowtime(id, request);
    }

    @PatchMapping("/{id}/status")
    public ShowtimeResponse updateShowtimeStatus(@RequestBody @Valid ShowtimeStatusUpdateRequest request, @PathVariable UUID id) {
        return showtimeService.updateShowtimeStatus(id, request);
    }

}
