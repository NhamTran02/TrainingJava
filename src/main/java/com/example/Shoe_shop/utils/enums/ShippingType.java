package com.example.Shoe_shop.utils.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum ShippingType {
    STANDARD(BigDecimal.valueOf(15000)),
    EXPRESS(BigDecimal.valueOf(20000)),
    FAST(BigDecimal.valueOf(30000));

    private final BigDecimal fee;

    ShippingType(BigDecimal fee) {
        this.fee = fee;
    }

    @JsonCreator
    public static ShippingType fromString(String value) {
        return ShippingType.valueOf(value.toUpperCase());
    }

}
