package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.PurchaseOrderDto;
import com.example.Shoe_shop.entity.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    PurchaseOrderDto toDto(PurchaseOrder entity);

    @Mapping(target = "items", ignore = true)
    PurchaseOrder toEntity(PurchaseOrderDto dto);
}
