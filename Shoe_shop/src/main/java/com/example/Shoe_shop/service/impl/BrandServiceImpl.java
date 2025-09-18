package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.BrandRequest;
import com.example.Shoe_shop.dto.response.BrandResponse;
import com.example.Shoe_shop.entity.Brand;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.BrandMapper;
import com.example.Shoe_shop.repository.BrandRepository;
import com.example.Shoe_shop.service.BrandService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandServiceImpl implements BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;
    EntityValidatorUtil entityValidatorUtil;

    @Override
    @Transactional
    public BrandResponse createBrand(BrandRequest request) {
        Brand brand = brandMapper.toEntity(request);
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brandMapper.toResponseList(brands);
    }

    @Override
    @Transactional
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        brandMapper.updateEntityFromRequest(request, brand);
        brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = entityValidatorUtil.requireBrand(id);

        if (!brand.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.BRAND_HAS_PRODUCTS);
        }

        brandRepository.delete(brand);
    }
}
