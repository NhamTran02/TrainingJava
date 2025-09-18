package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.entity.Token;
import com.example.Shoe_shop.repository.TokenRepository;
import com.example.Shoe_shop.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public Token save(Token token) {
        return tokenRepository.save(token);
    }

    @Override
    public Optional<Token> findByRefreshToken(String refreshToken) {
        return tokenRepository.findByRefreshToken(refreshToken);
    }

    @Override
    public Optional<Token> findByAccessToken(String accessToken) {
        return  tokenRepository.findByAccessToken(accessToken);
    }

    @Override
    public Optional<Token> findByUsername(String username) {
        return tokenRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public void blacklist(Token token) {
        token.setBlacklisted(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void blacklistAllByUsername(String username) {
        tokenRepository.findByUsernameAndBlacklistedFalse(username)
                .forEach(t -> {
                    t.setBlacklisted(true);
                    tokenRepository.save(t);
                });
    }
}
