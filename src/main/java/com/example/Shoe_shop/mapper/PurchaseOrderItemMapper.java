package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.PurchaseOrderItemDto;
import com.example.Shoe_shop.entity.PurchaseOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderItemMapper {
    @Mapping(source = "variant.id",target = "variantId")
    PurchaseOrderItemDto toDto(PurchaseOrderItem purchaseOrderItem);
}
