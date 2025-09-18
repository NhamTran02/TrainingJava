package com.example.Shoe_shop.dto.response.projection;

import java.math.BigDecimal;

public interface OrderSummaryProjection {
    Long getId();
    BigDecimal getTotalAmount();
    String getStatus();
    String getCreatedAt();
    BigDecimal getShippingFee();
    BigDecimal getTotalSum();
}
