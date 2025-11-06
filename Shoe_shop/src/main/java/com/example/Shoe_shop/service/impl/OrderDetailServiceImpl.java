package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.response.OrderDetailResponse;
import com.example.Shoe_shop.dto.response.OrderDetailView;
import com.example.Shoe_shop.dto.response.PaymentResponse;
import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.entity.Payment;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.service.OrderDetailService;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class OrderDetailServiceImpl implements OrderDetailService {
    EntityValidatorUtil entityValidatorUtil;

    @Override
    public OrderDetailView getOrderDetail(Long orderId, Long userId) {
        Order order=entityValidatorUtil.requireOrder(orderId);
        if(!CheckRole.isAdmin() && !order.getUser().getId().equals(userId)){
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        List<OrderDetailResponse> items=order.getOrderDetails().stream()
                .map(orderDetail -> new OrderDetailResponse(
                        orderDetail.getVariant().getProduct().getId(),
                        orderDetail.getVariant().getProduct().getName(),
                        orderDetail.getVariant().getVariantName(),
                        orderDetail.getQuantity(),
                        orderDetail.getUnitPrice(),
                        orderDetail.getUnitPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity()))
                )).toList();

        BigDecimal totalSum=order.getTotalAmount().add(order.getShippingFee());

        String paymentMethod = order.getPayments().stream()
                .max(Comparator.comparing(Payment::getCreatedAt))
                .map(p -> p.getPaymentMethod().name())
                .orElse("UNKNOWN");

        User buyer=order.getUser();

        return new OrderDetailView(
                order.getId(),
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getShippingFee(),
                totalSum,
                paymentMethod,
                order.getShippingAddress(),
                order.getTrackingNumber(),
                order.getNote(),
                buyer.getFullName(),
                buyer.getPhoneNumber(),
                items
        );
    }
}
