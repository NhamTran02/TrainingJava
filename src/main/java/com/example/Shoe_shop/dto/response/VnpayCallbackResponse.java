package com.example.Shoe_shop.dto.response;

import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VnpayCallbackResponse {
    Long orderId;
    String trackingNumber;
    OrderStatus orderStatus;
    PaymentStatus paymentStatus;
    BigDecimal amount;
    String message;
}
