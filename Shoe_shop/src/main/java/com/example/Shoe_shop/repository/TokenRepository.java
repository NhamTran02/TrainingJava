package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUsername(String username);
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    boolean existsByAccessToken(String accessToken);  // Cho check blacklist
}
