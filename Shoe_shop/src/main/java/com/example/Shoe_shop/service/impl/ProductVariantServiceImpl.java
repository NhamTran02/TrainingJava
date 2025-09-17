package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.ProductVariantRequest;
import com.example.Shoe_shop.dto.response.ProductVariantResponse;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.entity.ProductVariant;
import com.example.Shoe_shop.mapper.ProductVariantMapper;
import com.example.Shoe_shop.repository.ProductVariantRepository;
import com.example.Shoe_shop.service.ProductVariantService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductVariantServiceImpl implements ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductVariantMapper productVariantMapper;
    EntityValidatorUtil entityValidatorUtil;

    @Override
    @Transactional
    public ProductVariantResponse createProductVariant(ProductVariantRequest request) {
        Product product = entityValidatorUtil.requireProduct(request.getProductId());
        ProductVariant productVariant = productVariantMapper.toEntity(request);
        productVariant.setProduct(product);
        productVariantRepository.save(productVariant);
        return productVariantMapper.toResponse(productVariant);
    }
}
