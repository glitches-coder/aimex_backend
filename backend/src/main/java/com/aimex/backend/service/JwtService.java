package com.aimex.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes:120}") long expirationMinutes) {
        this.secretKey = createSecretKey(secret);
        this.expirationMinutes = expirationMinutes;
    }

    private SecretKey createSecretKey(String secret) {
        if (secret == null || secret.isBlank()) {
            // Generate a secure key if no secret is provided (for testing)
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        
        // JWT requires at least 256 bits (32 bytes) for HMAC-SHA256
        if (secretBytes.length < 32) {
            // Hash the secret to ensure it's at least 32 bytes
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashed = digest.digest(secretBytes);
                return Keys.hmacShaKeyFor(hashed);
            } catch (Exception e) {
                // Fallback: generate a secure key
                return Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        }
        
        return Keys.hmacShaKeyFor(secretBytes);
    }

    public String generateToken(String userId, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("uid", userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, String email) {
        String username = extractEmail(token);
        return username.equals(email) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = parseToken(token).getBody();
        return claimsResolver.apply(claims);
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }
}

