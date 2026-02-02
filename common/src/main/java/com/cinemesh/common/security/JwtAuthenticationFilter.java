package com.cinemesh.common.security;

import com.cinemesh.common.dto.UserDetailsDto;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull jakarta.servlet.FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = parseJwt(request);
        if (jwtService != null && jwtService.validateJwtToken(jwt)) {
            // 1. Giải mã token lấy dữ liệu
            Claims claims = jwtService.extractAllClaims(jwt);
            String email = claims.getSubject();
            String role = claims.get("roles", String.class);
            String userId = claims.get("userId", String.class);

            // 2. Tạo danh sách quyền (Authorities) từ thông tin trong Token
            // Lưu ý: Ta TIN TƯỞNG token, không cần query DB để check lại role
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

            // 3. Tạo một Principal đơn giản (User core của Spring Security)
            // Hoặc bạn có thể tạo một class UserDto đơn giản trong common để chứa info này
            UserDetailsDto principal = new UserDetailsDto(email, authorities);

            // 4. Tạo Authentication object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            // Có thể lưu thêm userId vào details để controller tiện lấy
            authentication.setDetails(userId);

            // 5. Set vào Context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

}
