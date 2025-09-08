package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "purchase_orders")
public class PurchaseOrder extends BaseId {
    @Column(name = "supplier_name")
    String supplierName;

    @Column(nullable = false,name = "total_cost")
    BigDecimal totalCost;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @OneToMany(mappedBy = "purchaseOrder")
    List<PurchaseOrderItem> items;
}
