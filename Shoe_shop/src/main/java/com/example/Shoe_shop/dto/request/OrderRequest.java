package com.example.Shoe_shop.dto.request;

import com.example.Shoe_shop.entity.ShippingMethod;
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String note;

    @NotBlank(message = "SHIPPING_ADDRESS_REQUIRED")
    String shippingAddress;

    @NotNull(message = "PAYMENT_METHOD_REQUIRED")
    PaymentMethod paymentMethod;

    @NotNull(message = "SHIPPING_METHOD_REQUIRED")
    ShippingMethod shippingMethod;

    @NotBlank(message = "TRACKING_NUMBER_BLANK")
    String trackingNumber;

    List<Long> cartItemIds;

}
