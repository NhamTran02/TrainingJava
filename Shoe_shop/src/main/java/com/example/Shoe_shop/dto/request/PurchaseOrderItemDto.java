package com.example.Shoe_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemDto {
    Long id;
    Long variantId;
    Integer quantity;
    Integer remainingQty;
    BigDecimal unitCost;
}
