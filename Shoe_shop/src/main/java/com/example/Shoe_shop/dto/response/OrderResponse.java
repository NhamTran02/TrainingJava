package com.example.Shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderResponse {
    Long id;
    BigDecimal totalAmount;
    String status;
    String createdAt;
    BigDecimal shippingFee;
    BigDecimal totalSum;
}
