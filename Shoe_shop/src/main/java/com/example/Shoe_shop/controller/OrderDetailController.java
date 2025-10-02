package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.OrderDetailView;
import com.example.Shoe_shop.service.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderDetailController {
    OrderDetailService orderDetailService;

    @GetMapping("/{orderId}/{userId}")
    @PreAuthorize("#userId== authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<OrderDetailView> getAllOrderDetail(@PathVariable Long orderId, @PathVariable Long userId){
        OrderDetailView orderDetailView = orderDetailService.getOrderDetail(orderId,userId);
        return ApiResponse.<OrderDetailView>builder()
                .result(orderDetailView)
                .build();
    }
}
