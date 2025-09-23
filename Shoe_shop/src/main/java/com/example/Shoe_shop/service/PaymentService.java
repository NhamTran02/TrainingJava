package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.RefundResult;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.dto.response.VnpayQueryResponse;
import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.entity.Payment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    Payment createPayment(Order order, OrderRequest orderRequest);
    String retryVnpayPayment(Long orderId, HttpServletRequest request);
//    String createVnpayPaymentUrl(Order order, HttpServletRequest request);
    String createVnpayPaymentUrl(Payment payment, HttpServletRequest request);
    VnpayCallbackResponse handleVnpayCallback(Map<String, String> params);
    void cancelExpiredVnpayPayments();
    void reconcilePendingVnpayPayments();
    VnpayQueryResponse manualCheckVnpayPayment(Long orderId);
    RefundResult refundPayment(Long paymentId);
}
