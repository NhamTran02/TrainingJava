package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_variants",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"product_id","size","color"})}
)
public class ProductVariant extends BaseId {

    @NotBlank(message = "Kích cỡ không được để trống")
    @Size(max = 10, message = "Kích cỡ tối đa 10 ký tự")
    @Column(nullable = false)
    String size;

    @NotBlank(message = "Màu sắc không được để trống")
    @Size(max = 50, message = "Màu sắc tối đa 50 ký tự")
    @Column(nullable = false)
    String color;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá gốc phải lớn hơn 0")
    @Column(nullable = false,name = "regular_price")
    BigDecimal regularPrice;

    @Column(name = "sale_price")
    BigDecimal salePrice;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được âm")
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
