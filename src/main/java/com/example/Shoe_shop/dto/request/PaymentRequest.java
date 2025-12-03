package com.example.Shoe_shop.dto.request;

import com.example.Shoe_shop.utils.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    Long orderId;
    PaymentMethod paymentMethod;
    BigDecimal amount;
    String note;
}

