package com.example.Shoe_shop.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    Long cartItemId;
    Long variantId;
    String productName;
    int quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
    Boolean selected;

}
