package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull(message = "VARIANT_ID_REQUIRED")
    private Long variantId;
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private int quantity;
}
