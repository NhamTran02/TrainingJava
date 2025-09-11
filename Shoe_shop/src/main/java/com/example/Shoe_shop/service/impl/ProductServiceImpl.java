package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductImageMapper;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.mapper.ProductVariantMapper;
import com.example.Shoe_shop.repository.BrandRepository;
import com.example.Shoe_shop.repository.CategoryRepository;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.specification.ProductSpecification;
import com.example.Shoe_shop.utils.CheckRole;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    BrandRepository brandRepository;
    CategoryRepository categoryRepository;
    ProductMapper productMapper;
    private final ProductVariantMapper productVariantMapper;
    private final ProductImageMapper productImageMapper;

    @Override
    @Transactional
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
    public PagedResponse<ProductResponse> searchProducts(ProductSearchRequest request, int page, int size, String sortBy, String sortDir) {
        Specification<Product> specification =
                ProductSpecification.searchProductSpecification(request, CheckRole.isAdmin());

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        Page<Product> products = productRepository.findAll(specification, pageable);

        List<ProductResponse> content = products.stream().map(p -> {
            ProductResponse r = productMapper.toResponse(p);
            r.setVariants(productVariantMapper.toResponseList(p.getVariants()));
            r.setImages(productImageMapper.toResponseList(p.getImages()));
            return r;
        }).toList();

        return new PagedResponse<>(
                content,
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                products.isLast()
        );
    }

//    @Override
//    public List<ProductResponse> searchProducts(ProductSearchRequest request) {
//        // Xây dựng Specification
//        Specification<Product> specification = ProductSpecification.searchProductSpecification(request, CheckRole.isAdmin());
//
//
//        // Thực hiện truy vấn với Specification
//        List<Product> products = productRepository.findAll(specification);
//
//        // Chuyển đổi sang ProductResponse
//        return products.stream().map(product -> {
//            ProductResponse response = productMapper.toResponse(product);
//            response.setVariants(productVariantMapper.toResponseList(product.getVariants()));
//            response.setImages(productImageMapper.toResponseList(product.getImages()));
//            return response;
//        }).toList();
//    }

    @Override
    @Transactional
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
    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if(product.getDeleted()){
            throw new AppException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }
        product.setDeleted(true);
        productRepository.save(product);
    }
}