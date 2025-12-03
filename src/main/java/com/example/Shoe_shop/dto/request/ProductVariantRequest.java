package com.example.Shoe_shop.dto.request;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    @NotBlank(message = "SIZE_INVALID")
    private String size;

    @NotBlank(message = "COLOR_INVALID")
    private String color;

    @NotNull(message = "REGULAR_PRICE_INVALID")
    @DecimalMin(value = "0.0", inclusive = false, message = "REGULAR_PRICE_GREATER_THAN_0")
    private BigDecimal regularPrice;

    private BigDecimal salePrice;

    @NotNull(message = "STOCK_INVALID")
    @Min(value = 0,message = "STOCK_QUANTITY_GREATER_THAN_0")
    private Integer stockQuantity;

    @NotNull(message = "PRODUCT_ID_INVALID")
    private Long productId;
}
