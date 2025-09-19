package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.entity.Payment;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.OrderRepository;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
public class VnpayController {
    private final OrderRepository orderRepository;

    @Value("${vnpay.secretKey}")
    private String vnpay_secretKey;

    @GetMapping("/vnpay-callback")
    public ApiResponse<VnpayCallbackResponse> vnpayCallback(@RequestParam Map<String, String> params) {
        // Lấy secure hash do VNPAY gửi (case-insensitive)
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            return ApiResponse.<VnpayCallbackResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("vnp_SecureHash is null")
                    .build();
        }

        params.remove("vnp_SecureHash");

        try {
            // Build hashData & verify chữ ký
            String hashData = buildHashDataString(params);
            String checkHash = hmacSHA512(vnpay_secretKey, hashData);

            if (!checkHash.equalsIgnoreCase(vnp_SecureHash)) {
                return ApiResponse.<VnpayCallbackResponse>builder()
                        .message("Invalid signature")
                        .build();
            }

            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");
            String amountStr = params.get("vnp_Amount");

            Order order = orderRepository.findByTrackingNumber(txnRef)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

            BigDecimal amount = new BigDecimal(amountStr).divide(BigDecimal.valueOf(100));

            // Lấy payment hiện tại của đơn
            Payment payment = order.getPayments().stream()
                    .max(Comparator.comparing(Payment::getCreatedAt))
                    .orElse(null);

            OrderStatus orderStatus;
            PaymentStatus paymentStatus;
            String message;

            if ("00".equals(responseCode)) {
                orderStatus = OrderStatus.PROCESSING;
                paymentStatus = PaymentStatus.SUCCESS;
                message = "Thanh toán thành công";
            } else {
                orderStatus = OrderStatus.CANCELLED;
                paymentStatus = PaymentStatus.FAILED;
                message = "Thanh toán thất bại";
            }

            order.setStatus(orderStatus);
            if (payment != null) {
                payment.setStatus(paymentStatus);
                payment.setResponseCode(responseCode);
                payment.setPayDate(java.time.LocalDateTime.now());
            }

            orderRepository.save(order);
            VnpayCallbackResponse response = VnpayCallbackResponse.builder()
                    .orderId(order.getId())
                    .trackingNumber(order.getTrackingNumber())
                    .orderStatus(orderStatus)
                    .paymentStatus(paymentStatus)
                    .amount(amount)
                    .message(message)
                    .build();
            return ApiResponse.<VnpayCallbackResponse>builder()
                    .result(response)
                    .build();

        } catch (Exception ex) {
            return ApiResponse.<VnpayCallbackResponse>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error verifying signature"+ex.getMessage())
                    .build();
        }
    }

    public String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException("hmac error", ex);
        }
    }

    //build hash string
    private String buildHashDataString(Map<String, String> params){
        // sắp xếp key
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) continue;
            // Dùng UTF-8 để encode giá trị (và giữ '+' do URLEncoder)
            String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);
            if (!first) {
                hashData.append('&');
            }
            hashData.append(fieldName).append('=').append(encodedValue);
            first = false;
        }
        return hashData.toString();
    }
}