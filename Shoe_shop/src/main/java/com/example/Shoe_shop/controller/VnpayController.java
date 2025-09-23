package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnpayController {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @GetMapping("/vnpay-callback")
    public ApiResponse<VnpayCallbackResponse> vnpayCallback(@RequestParam Map<String, String> params) {
        VnpayCallbackResponse response=paymentService.handleVnpayCallback(params);
        return ApiResponse.<VnpayCallbackResponse>builder()
                .code(200)
                .message("success")
                .result(response)
                .build();
    }
}