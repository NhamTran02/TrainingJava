package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.OrderResponse;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.service.OrderService;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ApiResponse<OrderResponse> createOrder(@PathVariable Long userId, @RequestBody OrderRequest orderRequest, HttpServletRequest request) {
        OrderResponse response=orderService.createOrderFromCart(userId,orderRequest,request);
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .result(response)
                .message("create order successfully")
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<List<OrderResponse>> getMyOrders(@PathVariable Long userId){
        List<OrderResponse> orders=orderService.getMyOrders(userId);
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orders)
                .build();
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PagedResponse<OrderResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ){
        PagedResponse<OrderResponse> response=orderService.getAllOrder(page,size,sortBy,sortDir);
        return ApiResponse.<PagedResponse<OrderResponse>>builder()
                .code(200)
                .result(response)
                .build();
    }

    @GetMapping("/status/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<PagedResponse<OrderResponse>> getOrderStatus(
            @PathVariable(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir)
    {
        Pageable pageable = PageRequest.of(
                page,
                size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending()
        );
        OrderStatus orderStatus = null;
        if (status != null && !status.isEmpty()) {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        }

        PagedResponse<OrderResponse> response= orderService.getOrdersbyStatus(userId,orderStatus,pageable);
        return ApiResponse.<PagedResponse<OrderResponse>>builder()
                .code(200)
                .result(response)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<OrderResponse> updateOrder(@PathVariable Long id,@RequestParam String status){
        OrderStatus newStatus=OrderStatus.valueOf(status.toUpperCase());
        OrderResponse response=orderService.updateOrderStatus(id,newStatus);
        return ApiResponse.<OrderResponse>builder()
                .code(200)
                .result(response)
                .build();
    }

    @PutMapping("/cancel/{orderId}")
    public ApiResponse<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("cancel order successfully")
                .build();
    }
}
