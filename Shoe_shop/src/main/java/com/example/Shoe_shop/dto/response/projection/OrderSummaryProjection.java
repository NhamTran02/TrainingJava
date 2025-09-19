package com.example.Shoe_shop.dto.response.projection;

import com.example.Shoe_shop.utils.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderSummaryProjection {
    Long getId();
    BigDecimal getTotalAmount();
    OrderStatus getStatus();
    LocalDateTime getCreatedAt();
    BigDecimal getShippingFee();
    String getTrackingNumber();
}
