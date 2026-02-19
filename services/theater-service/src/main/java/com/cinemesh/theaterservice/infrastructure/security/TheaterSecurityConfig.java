package com.cinemesh.theaterservice.infrastructure.security;

import com.cinemesh.common.security.JwtAuthenticationFilter;
import com.cinemesh.common.statics.RoleName;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // <--- Kích hoạt @PreAuthorize
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TheaterSecurityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/rooms").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/rooms/{id}").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/rooms/{id}/status").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/rooms/{id}").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/rooms").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
