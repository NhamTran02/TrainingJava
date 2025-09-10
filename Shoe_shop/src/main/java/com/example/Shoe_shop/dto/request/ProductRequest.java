package com.example.Shoe_shop.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "PRODUCT_NAME_INVALID")
    private String name;

    private String description;

    @NotNull(message = "BRAND_ID_INVALID")
    private Long brandId;

    @NotNull(message = "CATEGORY_ID_INVALID")
    private Long categoryId;
}