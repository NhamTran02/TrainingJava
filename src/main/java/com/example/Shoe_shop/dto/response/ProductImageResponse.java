package com.example.Shoe_shop.dto.response;

import lombok.Data;

@Data
public class ProductImageResponse {
    private Long id;
    private String url;
    private Boolean isThumbnail;
    private Long productId;
}