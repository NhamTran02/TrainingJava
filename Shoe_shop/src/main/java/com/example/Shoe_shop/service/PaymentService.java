package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.PaymentRequest;
import com.example.Shoe_shop.dto.response.PaymentResponse;
import com.example.Shoe_shop.dto.response.RefundResult;
import com.example.Shoe_shop.dto.response.VnpayCallbackResponse;
import com.example.Shoe_shop.dto.response.VnpayQueryResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    String retryVnpayPayment(Long orderId, HttpServletRequest request);
    List<PaymentResponse> getPayments(Long orderId);
    String createVnpayPaymentUrl(PaymentResponse paymentResponse, HttpServletRequest request);
    VnpayCallbackResponse handleVnpayCallback(Map<String, String> params);
    void cancelExpiredVnpayPayments();
    void reconcilePendingVnpayPayments();
    VnpayQueryResponse manualCheckVnpayPayment(Long orderId);
    RefundResult refundPayment(Long paymentId);
    void sendReminderForNearlyExpiredPayments();
}
