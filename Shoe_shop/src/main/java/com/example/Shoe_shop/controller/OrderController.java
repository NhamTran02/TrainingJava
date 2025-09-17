package com.example.Shoe_shop.controller;


import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.OrderResponse;
import com.example.Shoe_shop.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<OrderResponse> createOrder(@PathVariable Long userId,@RequestBody OrderRequest orderRequest){
        OrderResponse response=orderService.createOrderFromCart(userId,orderRequest);
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .result(response)
                .message("create order successfully")
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getOrders(@PathVariable Long userId){
        List<OrderResponse> orders=orderService.getMyOrders(userId);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders)
                .build();
    }
}
