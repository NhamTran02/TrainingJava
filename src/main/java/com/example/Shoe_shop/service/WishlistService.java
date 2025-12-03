package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.response.ProductResponse;

import java.util.List;


public interface WishlistService {
    void addToWishlist(Long userId, Long productId);
    void removeFromWishlist(Long userId, Long productId);
    List<ProductResponse> getWishlist(Long userId);
}
