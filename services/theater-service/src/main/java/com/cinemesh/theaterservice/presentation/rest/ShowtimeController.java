package com.cinemesh.theaterservice.presentation.rest;


import com.cinemesh.theaterservice.application.dto.request.ShowtimeCreationRequest;
import com.cinemesh.theaterservice.application.dto.response.ShowtimeResponse;
import com.cinemesh.theaterservice.application.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
