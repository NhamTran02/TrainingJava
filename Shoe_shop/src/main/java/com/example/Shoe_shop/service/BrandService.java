package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.BrandRequest;
import com.example.Shoe_shop.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse createBrand(BrandRequest request);
    BrandResponse getBrandById(Long id);
    List<BrandResponse> getAllBrands();
    BrandResponse updateBrand(Long id, BrandRequest request);
    void deleteBrand(Long id);
}
