package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.OrderResponse;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderService {
    OrderResponse createOrderFromCart(Long userId,OrderRequest orderRequest);
    List<OrderResponse> getMyOrders(Long userId);
    PagedResponse<OrderResponse> getAllOrder(int page, int size, String sortBy, String sortDir);
    PagedResponse<OrderResponse> getOrdersbyStatus(Long userId, OrderStatus status, Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus newStatus);
    void cancelOrder(Long orderId);

}
