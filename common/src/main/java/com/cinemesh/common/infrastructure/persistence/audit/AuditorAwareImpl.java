package com.cinemesh.common.infrastructure.persistence.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

// T ở đây thường là String (Username/Email) hoặc UUID (UserID)
public class AuditorAwareImpl implements AuditorAware<String> {

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
        return Optional.ofNullable(authentication.getName());
    }
}