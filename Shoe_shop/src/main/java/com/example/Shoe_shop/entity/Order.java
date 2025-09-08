package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import com.example.Shoe_shop.utils.OrderStatus;
import com.example.Shoe_shop.utils.PaymentMethod;
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

    @NotBlank(message = "Địa chỉ không được bỏ trống")
    @Column(name = "shipping_address")
    String shippingAddress;

    String note;

    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải 10 số")
    @Column(name = "tracking_number")
    String trackingNumber;

    @Column(name = "shipping_fee")
    BigDecimal shippingFee = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    List<Review> reviews;
}
