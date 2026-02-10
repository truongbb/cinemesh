package com.cinemesh.common.infrastructure.persistence.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Kích hoạt Auditing
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider(ObjectMapper objectMapper) {
        return new AuditorAwareImpl(objectMapper);
    }
}
