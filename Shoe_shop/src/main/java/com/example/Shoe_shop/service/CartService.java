package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.CartItemRequest;
import com.example.Shoe_shop.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartByUserId(Long userId);
    CartResponse  addorUpdateItemToCart(CartItemRequest request, Long userId);
    CartResponse  removeItemFromCart(CartItemRequest request, Long userId);
    CartResponse clearCart(Long cartId);
}
