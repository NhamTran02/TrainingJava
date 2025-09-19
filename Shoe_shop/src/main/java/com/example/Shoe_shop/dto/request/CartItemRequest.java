package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemRequest {
    @NotNull(message = "VARIANT_ID_REQUIRED")
    Long variantId;
    @Min(value = 1, message = "QUANTITY_MIN_1")
    int quantity;
    Boolean selected = false;
}
