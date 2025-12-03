package com.example.Shoe_shop.service;

import com.example.Shoe_shop.entity.Token;

import java.util.Optional;

public interface TokenService {
    Token save(Token token);

    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByUsername(String username);
    void blacklist(Token token);
    void blacklistAllByUsername(String username);
}
