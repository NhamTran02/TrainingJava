package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import com.example.Shoe_shop.utils.enums.ShippingType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "shipping_methods")
public class ShippingMethod extends BaseId {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ShippingType methodName;

    @Column(nullable = false)
    BigDecimal fee;

    @OneToMany(mappedBy = "shippingMethod")
    List<Order> orders;

    // constructor để set fee tự động theo enum
    public ShippingMethod(ShippingType methodName) {
        this.methodName = methodName;
        this.fee = methodName.getFee();
    }
}
