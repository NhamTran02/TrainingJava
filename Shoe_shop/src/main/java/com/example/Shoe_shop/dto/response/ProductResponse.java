package com.example.Shoe_shop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Boolean deleted;
    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
}
