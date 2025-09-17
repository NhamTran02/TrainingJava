package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
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

    @PrePersist
    public void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
