package com.example.Shoe_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDto {
    Long id;
    String supplierName;
    BigDecimal totalCost;
    LocalDateTime orderDate;
    List<PurchaseOrderItemDto> items;
}
