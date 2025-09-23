package com.example.Shoe_shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelOrderResponse {
    private Long orderId;
    private String message;             // Thông báo hủy đơn
    private boolean refundSuccess;      // Kết quả hoàn tiền
    private String refundCode;          // Mã phản hồi từ VNPAY (00 = thành công)
    private String refundMessage;       // Thông điệp từ VNPAY
    private String refundTransactionNo; // Mã giao dịch hoàn tiền
    private String originalTxnRef;      // Mã tham chiếu đơn hàng ban đầu
    private BigDecimal amount;                // Số tiền hoàn (VNĐ)
    private String refundTime;          // Thời gian hoàn tiền ISO-8601
}