package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByUsername(String username);
    List<Token> findByUsernameAndBlacklistedFalse(String username);
}
