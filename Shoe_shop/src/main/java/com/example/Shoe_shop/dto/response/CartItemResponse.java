package com.example.Shoe_shop.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private Long variantId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
