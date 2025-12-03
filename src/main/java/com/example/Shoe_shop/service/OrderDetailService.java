package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.response.OrderDetailView;

public interface OrderDetailService{
    OrderDetailView getOrderDetail(Long orderId,Long userId);
}
