package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tokens")
public class Token extends BaseId {
    @Column(unique = true)
    String username;
    @Column(name = "access_token")
    String accessToken;
    @Column(name = "refresh_token")
    String refreshToken;
    private boolean blacklisted = false;
}
