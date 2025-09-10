package com.example.Shoe_shop.mapper;


import com.example.Shoe_shop.dto.request.ProductVariantRequest;
import com.example.Shoe_shop.dto.response.ProductVariantResponse;
import com.example.Shoe_shop.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    ProductVariant toEntity(ProductVariantRequest request);

    ProductVariantResponse toResponse(ProductVariant variant);

    void updateEntityFromRequest(ProductVariantRequest request, @MappingTarget ProductVariant variant);

    List<ProductVariantResponse> toResponseList(List<ProductVariant> variants);
}
