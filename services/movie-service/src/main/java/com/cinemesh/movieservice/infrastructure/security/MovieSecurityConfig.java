package com.cinemesh.movieservice.infrastructure.security;

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
public class MovieSecurityConfig {

    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Mặc định cho phép Swagger chạy
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/movie-genres").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.PUT, "/api/v1/movie-genres/{id}").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/movie-genres/{id}").hasAnyAuthority(RoleName.ROLE_ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/movie-genres").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/movie-genres/{id}").permitAll()
                        // Còn lại chặn hết
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
