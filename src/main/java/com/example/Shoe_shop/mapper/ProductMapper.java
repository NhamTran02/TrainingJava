package com.example.Shoe_shop.mapper;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses =  {ProductVariantMapper.class, ProductImageMapper.class})
public interface ProductMapper {
    @Mapping(target = "deleted", constant = "false")
    Product toEntity(ProductRequest request);

    @Mapping(source = "brand.id", target = "brandId")
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);


    List<ProductResponse> toResponseList(List<Product> products);
}
