package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.PaymentRequest;
import com.example.Shoe_shop.dto.response.PaymentResponse;
import com.example.Shoe_shop.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "order", ignore = true)
    Payment toEntity(PaymentRequest request);

    @Mapping(source = "order.id", target = "orderId")
    PaymentResponse toResponse(Payment payment);
}
