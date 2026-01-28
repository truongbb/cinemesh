package com.cinemesh.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application.security")
public class JwtProperties {
    private TokenConfig accessToken;
    private TokenConfig refreshToken;

    @Data
    public static class TokenConfig {
        private String secretKey;
        private long tokenValidityMilliseconds;
    }
}
