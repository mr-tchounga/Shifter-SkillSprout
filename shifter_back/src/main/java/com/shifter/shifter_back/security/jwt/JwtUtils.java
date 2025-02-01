package com.shifter.shifter_back.security.jwt;

import com.shifter.shifter_back.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

@Component
@Slf4j
public class JwtUtils {
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

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    public String generateTokenFromEmail(String email) {
        return Jwts.builder().setClaims(new HashMap<>())
                .setSubject(email).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Long.parseLong(jwtExpirationMs)))
                .signWith(key(), SignatureAlgorithm.HS512)
                .compact();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJwt(authToken);
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
