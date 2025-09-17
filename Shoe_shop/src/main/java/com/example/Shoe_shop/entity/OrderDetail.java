package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order_details")
public class OrderDetail extends BaseId {

    @Min(value = 1, message = "QUANTITY_MIN_1")
    @Column(nullable = false)
    Integer quantity;

    @Column(nullable = false,name = "unit_price")
    BigDecimal unitPrice;

    @Column(nullable = false,name = "unit_cost")
    BigDecimal unitCost;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    ProductVariant variant;
}
