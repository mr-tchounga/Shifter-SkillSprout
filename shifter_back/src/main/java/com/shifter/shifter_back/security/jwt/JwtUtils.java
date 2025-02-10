package com.shifter.shifter_back.security.jwt;

import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.auth.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtils {
    private final UserRepository userRepository;


    @Value("${token.signing.key}")
    private String jwtSecret;
    @Value("${token.signing.expiration}")
    private String jwtExpirationMs;

    @PostConstruct
    public void validateConfig() {
        log.info("Validating JWT configuration...");
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret key is not configured properly!");
        }
        if (jwtExpirationMs == null || jwtExpirationMs.isEmpty()) {
            throw new IllegalStateException("JWT expiration time is not configured properly!");
        }
        log.info("JWT configuration is valid.");
    }

    public String generateJwtToken(User user) {
        return generateTokenFromEmail(user.getEmail());
    }
    public Key key() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT Secret Key is missing or not configured properly!");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Secret Key! Make sure it's Base64-encoded.", e);
        }
    }

    public String generateTokenFromEmail(String email) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(Long.parseLong(jwtExpirationMs));

        try {
            return Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(expiration))
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
        } catch (IllegalArgumentException e) {
            log.error("JWT generation failed: Invalid argument", e);
            throw e;  // Re-throw or handle appropriately
        }
    }


    public String getEmailFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key()).build()
                    .parseClaimsJws(token).getBody().getSubject();
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("JWT Token is expired!", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("Malformed JWT Token!", e);
        } catch (UnsupportedJwtException e) {
            throw new IllegalArgumentException("JWT Token is unsupported!", e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("Invalid JWT signature!", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token!", e);
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid Jwt signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Jwt token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Jwt token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Jwt claims string is empty: {}", e.getMessage());
        }
        return false;
    }


}
