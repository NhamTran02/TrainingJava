package com.example.Shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailView {
    Long orderId;
    String status;
    LocalDateTime createdAt;
    BigDecimal totalAmount;
    BigDecimal shippingFee;
    BigDecimal totalSum;
    String paymentMethod;
    String shippingAddress;
    String trackingNumber;
    String note;
    String buyerName;
    String buyerPhone;
    List<OrderDetailResponse> items;
}
