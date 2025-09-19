package com.example.Shoe_shop.utils.enums;

public enum PaymentStatus {
    PENDING,    // Đang chờ thanh toán (mới tạo URL nhưng chưa callback thành công)
    SUCCESS,       // Đã thanh toán thành công
    FAILED,     // Thanh toán thất bại (callback trả lỗi hoặc hết hạn)
    REFUNDED    // Đã hoàn tiền (nếu có xử lý refund)
}
