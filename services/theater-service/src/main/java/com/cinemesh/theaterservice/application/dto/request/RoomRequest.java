package com.cinemesh.theaterservice.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRequest {

    @NotBlank(message = "Room name is required")
    @Length(max = 255, message = "Room name must be less than 255 characters")
    String name;

    List<@Valid SeatRequest> seats;

}
