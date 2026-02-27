package com.cinemesh.bookingservice.presentation.rest;

import com.cinemesh.bookingservice.application.dto.request.BookingCreationRequest;
import com.cinemesh.bookingservice.application.dto.response.OrderResponse;
import com.cinemesh.bookingservice.application.dto.response.ShowtimeSeatResponse;
import com.cinemesh.bookingservice.application.service.BookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public OrderResponse createBooking(@RequestBody @Valid BookingCreationRequest request) {
        return bookingService.createBooking(request);
    }

}
