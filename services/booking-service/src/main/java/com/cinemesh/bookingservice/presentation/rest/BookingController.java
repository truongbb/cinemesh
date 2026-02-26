package com.cinemesh.bookingservice.presentation.rest;

import com.cinemesh.bookingservice.application.dto.response.ShowtimeSeatResponse;
import com.cinemesh.bookingservice.application.service.BookingService;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {

    BookingService bookingService;

    @GetMapping("/showtimes/{showtimeId}/seats")
    public ShowtimeSeatResponse getSeatsOfShowtime(@PathVariable @NotNull UUID showtimeId) {
        return bookingService.getSeatsOfShowtime(showtimeId);
    }

}
