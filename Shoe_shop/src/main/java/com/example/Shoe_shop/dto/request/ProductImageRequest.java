package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductImageRequest {
    @NotBlank(message = "IMAGE_URL_INVALID")
    private String url;

    @NotNull(message = "PRODUCT_ID_INVALID")
    private Long productId;

    private Boolean isThumbnail = false;
}
