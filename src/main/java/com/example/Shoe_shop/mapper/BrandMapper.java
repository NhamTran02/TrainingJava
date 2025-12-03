package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.BrandRequest;
import com.example.Shoe_shop.dto.response.BrandResponse;
import com.example.Shoe_shop.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toEntity(BrandRequest request);
    BrandResponse toResponse(Brand brand);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntityFromRequest(BrandRequest request, @MappingTarget Brand brand);

    List<BrandResponse> toResponseList(List<Brand> brands);
}
