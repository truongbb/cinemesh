package com.cinemesh.common.security;


import com.cinemesh.common.dto.UserClaimsDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {

    JwtProperties jwtProperties;

    public String generateJwtToken(UserClaimsDto userClaims, boolean refreshToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", String.join(", ", userClaims.getRoles().stream().map(r -> r.getName().name()).toList()));
        claims.put("userId", userClaims.getUserId());
        // claims.put("fullName", userClaims.getFullName()); // Ví dụ mở rộng

        Key key = refreshToken ? refreshTokenKey() : accessTokenKey();
        long expiration = refreshToken ? jwtProperties.getRefreshToken().getTokenValidityMilliseconds()
                : jwtProperties.getAccessToken().getTokenValidityMilliseconds();

        Date expirationDate = Date.from(LocalDateTime.now().plusSeconds(expiration / 1000).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userClaims.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private Key accessTokenKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccessToken().getSecretKey()));
    }

    private Key refreshTokenKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefreshToken().getSecretKey()));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(accessTokenKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(accessTokenKey()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    /**
     * Extracts all claims from a JWT token, verifying the signature.
     *
     * @param token The JWT string.
     * @return The Claims object containing the payload data.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessTokenKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a specific claim by key.
     *
     * @param token    The JWT string.
     * @param claimKey The name of the claim (e.g., "sub", "name", "userId").
     * @return The value of the claim.
     */
    public <T> T extractClaim(String token, String claimKey, Class<T> requiredType) {
        final Claims claims = extractAllClaims(token);
        return claims.get(claimKey, requiredType);
    }

    /**
     * Extracts the username (subject claim) from the token.
     *
     * @param token The JWT string.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims.SUBJECT, String.class);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token The JWT string.
     * @return The expiration Date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims.EXPIRATION, Date.class);
    }


}
