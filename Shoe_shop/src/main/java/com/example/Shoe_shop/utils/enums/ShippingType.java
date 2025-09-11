package com.example.Shoe_shop.utils.enums;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum ShippingType {
    STANDARD(BigDecimal.valueOf(15)),
    EXPRESS(BigDecimal.valueOf(20)),
    FAST(BigDecimal.valueOf(30));

    private final BigDecimal fee;

    ShippingType(BigDecimal fee) {
        this.fee = fee;
    }

}
