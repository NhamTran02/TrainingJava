package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order_details")
public class OrderDetail extends BaseId {

    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
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
