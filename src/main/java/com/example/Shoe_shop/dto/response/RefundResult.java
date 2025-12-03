package com.example.Shoe_shop.dto.response;

import com.example.Shoe_shop.utils.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResult {
    private String transactionId;    // Mã giao dịch VNPAY
    private Long paymentId;          // ID payment trong hệ thống
    private PaymentStatus refundStatus;     // SUCCESS / FAILED
    private BigDecimal refundAmount;     // Số tiền hoàn
    private LocalDateTime refundDate;       // Thời gian hoàn tiền
    private String vnpResponseCode;  // Mã phản hồi từ VNPAY
}