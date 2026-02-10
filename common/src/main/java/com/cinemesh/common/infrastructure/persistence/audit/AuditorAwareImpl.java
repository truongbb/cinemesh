package com.cinemesh.common.infrastructure.persistence.audit;

import com.cinemesh.common.dto.SimpleUserDetailsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@AllArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Nếu chưa login hoặc là user ẩn danh (anonymous) -> Trả về SYSTEM hoặc null
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.of("SYSTEM"); // Hoặc return Optional.empty();
        }

        // Trả về username (email) của người dùng
//        UserDetailsDto userDetailsDto = (UserDetailsDto) authentication.getPrincipal();
        String userDetails = objectMapper.writeValueAsString(authentication.getPrincipal());
        SimpleUserDetailsDto userDetailsDto = objectMapper.readValue(userDetails, SimpleUserDetailsDto.class);
        return Optional.ofNullable(userDetailsDto.getEmail());
    }
}