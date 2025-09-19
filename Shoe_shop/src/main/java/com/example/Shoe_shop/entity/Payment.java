package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import com.example.Shoe_shop.utils.enums.PaymentMethod;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseAudit {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency = "VND";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "txn_ref", nullable = false, unique = true)
    private String txnRef; // vnp_TxnRef

    @Column(name = "response_code")
    private String responseCode; // vnp_ResponseCode

    @Column(name = "pay_date")
    private LocalDateTime payDate;

    private String note;

}
