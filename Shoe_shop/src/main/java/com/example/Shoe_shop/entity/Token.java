package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "tokens")
public class Token extends BaseId {
    @Column(unique = true)
    private String username;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    private String refreshToken;
    private boolean blacklisted = false;
}
