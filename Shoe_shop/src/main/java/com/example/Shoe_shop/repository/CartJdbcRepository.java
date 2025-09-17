package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Cart;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Cart {
    Optional<Cart> findByUser_Id(Long id);
}
