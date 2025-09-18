package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.OrderRequest;
import com.example.Shoe_shop.dto.response.OrderResponse;
import com.example.Shoe_shop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // Không set trackingNumber, shippingFee vì tạo bên Service
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "orderDetails", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "user", ignore = true)
    Order toEntity(OrderRequest orderRequest);

    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", expression = "java(order.getCreatedAt().toString())")
    @Mapping(target = "totalSum",expression = "java(order.getTotalAmount().add(order.getShippingFee()))")
    OrderResponse toResponse(Order order);

}
