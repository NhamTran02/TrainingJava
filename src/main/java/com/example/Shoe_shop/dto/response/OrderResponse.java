package com.example.Shoe_shop.dto.response;

import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    Long id;
    BigDecimal totalAmount;
    OrderStatus status;
    LocalDateTime createdAt;
    BigDecimal shippingFee;
    String trackingNumber;
    String paymentUrl;
}
