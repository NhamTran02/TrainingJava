package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
public class Order extends BaseAudit {

    @Column(nullable = false,name = "total_amount")
    BigDecimal totalAmount;

    @Column(nullable = false,name = "total_cost")
    BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatus status = OrderStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "payment_method")
    PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    ShippingMethod shippingMethod;

    @NotBlank(message = "ADDRESS_INVALID")
    @Column(name = "shipping_address")
    String shippingAddress;

    String note;

    @NotBlank(message = "TRACKING_NUMBER_BLANK")
    @Column(name = "tracking_number",unique = true,nullable = false)
    String trackingNumber;

    @Column(name = "shipping_fee")
    BigDecimal shippingFee = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    List<Review> reviews;

}
