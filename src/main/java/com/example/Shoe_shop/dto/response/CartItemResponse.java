package com.example.Shoe_shop.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    Long cartItemId;
    Long productId;
    ProductVariantResponse variantResponse;
    String productName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
    Boolean selected;
    String imageUrl;
}
