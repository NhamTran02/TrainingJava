package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_variants",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id","size","color"})}
)
public class ProductVariant extends BaseId {

    @NotBlank(message = "SIZE_INVALID")
    @Column(nullable = false)
    String size;

    @NotBlank(message = "COLOR_INVALID")
    @Column(nullable = false)
    String color;

    @NotNull(message = "REGULAR_PRICE_INVALID")
    @DecimalMin(value = "0.0", inclusive = false, message = "REGULAR_PRICE_GREATER_THAN_0")
    @Column(nullable = false,name = "regular_price")
    BigDecimal regularPrice;

    @Column(name = "sale_price")
    BigDecimal salePrice;

    @NotNull(message = "STOCK_INVALID")
    @Min(value = 0, message = "STOCK_QUANTITY_GREATER_THAN_0")
    @Column(nullable = false,name = "stock_quantity")
    Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @OneToMany(mappedBy = "variant")
    List<CartItem> cartItems;

    @OneToMany(mappedBy = "variant")
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "variant")
    List<PurchaseOrderItem> purchaseOrderItems;

}
