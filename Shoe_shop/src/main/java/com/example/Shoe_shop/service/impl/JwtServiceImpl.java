package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.accessKey}")
    private String accessKeyBase64;

    @Value("${jwt.refreshKey}")
    private String refreshKeyBase64;

    @Value("${jwt.expiryHour}")
    private long accessExpiryMinutes;

    @Value("${jwt.expiryDay}")
    private long refreshExpiryDays;

    private Key accessKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(accessKeyBase64);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // fallback nếu accessKey không phải base64
            return Keys.hmacShaKeyFor(accessKeyBase64.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Key refreshKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(refreshKeyBase64);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            return Keys.hmacShaKeyFor(refreshKeyBase64.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .claim("roles", List.of(user.getRole().getRoleName()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(accessExpiryMinutes, ChronoUnit.HOURS)))
                .signWith(accessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpiryDays, ChronoUnit.DAYS)))
                .signWith(refreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String getUsernameFromAccessToken(String token) {
        return Jwts.parserBuilder().setSigningKey(accessKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parserBuilder().setSigningKey(refreshKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            String username = getUsernameFromAccessToken(token);
            return username.equals(userDetails.getUsername());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
