package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.ProductVariantRequest;
import com.example.Shoe_shop.dto.response.ProductVariantResponse;

public interface ProductVariantService {
    ProductVariantResponse createProductVariant(ProductVariantRequest request);
}
