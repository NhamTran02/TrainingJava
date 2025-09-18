package com.example.Shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    Long productId;
    String productName;
    String variant;
    Integer quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
