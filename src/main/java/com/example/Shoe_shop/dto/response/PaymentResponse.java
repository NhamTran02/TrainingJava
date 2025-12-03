package com.example.Shoe_shop.dto.response;

import com.example.Shoe_shop.utils.enums.PaymentMethod;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Long paymentId;
    Long orderId;
    PaymentMethod paymentMethod;
    BigDecimal amount;
    PaymentStatus status;
    String txnRef;
    String transactionNo;
    LocalDateTime payDate;
    String note;
}
