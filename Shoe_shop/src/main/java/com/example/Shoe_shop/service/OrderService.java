package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    public OrderResponse createOrderFromCart(Long userId,OrderRequest orderRequest);
    public List<OrderResponse> getMyOrders(Long userId);
}
