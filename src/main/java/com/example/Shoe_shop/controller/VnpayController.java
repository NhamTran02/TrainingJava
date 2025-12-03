package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.service.PaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnpayController {
    private final PaymentService paymentService;

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        VnpayCallbackResponse callbackResponse = paymentService.handleVnpayCallback(params);
        
        // Redirect trực tiếp về trang orders với thông tin thanh toán
        String frontendUrl = "http://localhost:4200/orders";
        String redirectUrl = String.format("%s?payment=success&orderId=%d&amount=%s&trackingNumber=%s",
                frontendUrl,
                callbackResponse.getOrderId(),
                callbackResponse.getAmount().toString(),
                callbackResponse.getTrackingNumber()
        );
    
        response.sendRedirect(redirectUrl);
    }
}