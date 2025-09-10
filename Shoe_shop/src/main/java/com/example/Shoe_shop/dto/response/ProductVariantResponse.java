package com.example.Shoe_shop.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantResponse {
    private Long id;
    private String size;
    private String color;
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    private Integer stockQuantity;
    private Long productId;
}