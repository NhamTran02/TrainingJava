package com.example.Shoe_shop.service;

import com.example.Shoe_shop.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String getUsernameFromAccessToken(String token);
    String getUsernameFromRefreshToken(String token);
    boolean validateAccessToken(String token, UserDetails userDetails);
    boolean validateRefreshToken(String token);
}
