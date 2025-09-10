package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.entity.Brand;
import com.example.Shoe_shop.entity.Category;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.repository.BrandRepository;
import com.example.Shoe_shop.repository.CategoryRepository;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.utils.CheckRole;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = productMapper.toEntity(request);
        product.setBrand(brand);
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if(!CheckRole.isAdmin() && product.getDeleted()){
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> searchProducts(ProductRequest request, Pageable pageable) {
        return null;
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        productMapper.updateEntityFromRequest(request, product);
        product.setBrand(brand);
        product.setCategory(category);
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setDeleted(true);
        productRepository.save(product);
    }
}