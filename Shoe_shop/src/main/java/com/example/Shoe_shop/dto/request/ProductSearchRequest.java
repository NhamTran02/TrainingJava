package com.example.Shoe_shop.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchRequest {
    private String keyword;
    private Long categoryId;
    private Long brandId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String size;
    private String color;
}
