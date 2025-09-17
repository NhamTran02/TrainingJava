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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "cart_items")
public class CartItem extends BaseId {
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    Cart cart;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    ProductVariant variant;

    @Min(value = 1,message = "QUANTITY_MIN_1")
    @Column(nullable = false)
    Integer quantity;
}
