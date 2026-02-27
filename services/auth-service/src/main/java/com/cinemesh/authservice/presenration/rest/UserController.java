package com.cinemesh.authservice.presenration.rest;

import com.cinemesh.authservice.application.dto.response.UserResponse;
import com.cinemesh.authservice.application.service.UserService;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/{id}/activation")
    public void activateUser(@PathVariable UUID id) {
        userService.activateUser(id);
    }

    @GetMapping("/email")
    public UserResponse getUserByEmail(@RequestParam @NotEmpty(message = "Email required") String email) {
        return userService.getUserByEmail(email);
    }

}
