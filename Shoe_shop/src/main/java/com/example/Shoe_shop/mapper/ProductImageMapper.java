package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.ProductImageRequest;
import com.example.Shoe_shop.dto.response.ProductImageResponse;
import com.example.Shoe_shop.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageRequest request);

    @Mapping(source = "product.id", target = "productId")
    ProductImageResponse toResponse(ProductImage image);

    void updateEntityFromRequest(ProductImageRequest request, @MappingTarget ProductImage image);

    List<ProductImageResponse> toResponseList(List<ProductImage> images);
}