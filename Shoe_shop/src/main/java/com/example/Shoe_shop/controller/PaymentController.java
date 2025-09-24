package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.VnpayQueryResponse;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/retry/{orderId}")
    @PreAuthorize("@PaymentSecurity.hasAccess(#orderId,authentication.name)")
    public ApiResponse<Map<String, String>> retryPayment(
            @PathVariable Long orderId,
            HttpServletRequest request) {

        String vnpayUrl = paymentService.retryVnpayPayment(orderId, request);
        Map<String, String> resp = new HashMap<>();
        resp.put("paymentUrl", vnpayUrl);
        return ApiResponse.<Map<String, String>>builder()
                .result(resp)
                .build();
    }

    @GetMapping("/check-status/{orderId}")
    @PreAuthorize("@PaymentSecurity.hasAccess(#orderId,authentication.name)")
    public ApiResponse<VnpayQueryResponse> checkStatus(@PathVariable Long orderId) {
        VnpayQueryResponse resp = paymentService.manualCheckVnpayPayment(orderId);
        return ApiResponse.<VnpayQueryResponse>builder()
                .code(200)
                .message(resp == null ? "Không nhận được phản hồi từ VNPAY" : "OK")
                .result(resp)
                .build();
    }

}
