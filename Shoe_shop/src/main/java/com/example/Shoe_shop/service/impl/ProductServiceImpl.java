package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductImageResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.dto.response.ProductVariantResponse;
import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductImageMapper;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.mapper.ProductVariantMapper;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.specification.ProductSpecification;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
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
    ProductMapper productMapper;
    ProductVariantMapper productVariantMapper;
    ProductImageMapper productImageMapper;
    EntityValidatorUtil entityValidatorUtil;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Brand brand = entityValidatorUtil.requireBrand(request.getBrandId());
        Category category = entityValidatorUtil.requireCategory(request.getCategoryId());

        Product product = productMapper.toEntity(request);
        product.setBrand(brand);
        product.setCategory(category);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = entityValidatorUtil.requireProduct(id);
        if(!CheckRole.isAdmin() && product.getDeleted()){
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        List<ProductVariantResponse> variantResponses=productVariantMapper.toResponseList(product.getVariants());

        List<ProductImageResponse> imageResponses = productImageMapper.toResponseList(product.getImages());
        return productMapper.toResponse(product);
    }



    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(ProductSearchRequest request, int page, int size, String sortBy, String sortDir) {
        Specification<Product> specification = ProductSpecification.buildSpecification(
                        request.getKeyword(),
                        request.getCategoryId(),
                        request.getBrandId(),
                        request.getMinPrice(),
                        request.getMaxPrice(),
                        request.getSize(),
                        request.getColor(),
                        CheckRole.isAdmin());

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
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                content
        );
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = entityValidatorUtil.requireProduct(id);

        Brand brand = entityValidatorUtil.requireBrand(request.getBrandId());
        Category category = entityValidatorUtil.requireCategory(request.getCategoryId());

        productMapper.updateEntityFromRequest(request, product);
        product.setBrand(brand);
        product.setCategory(category);
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void softDeleteProduct(Long id) {
        Product product = entityValidatorUtil.requireProduct(id);
        if(product.getDeleted()){
            throw new AppException(ErrorCode.PRODUCT_ALREADY_DELETED);
        }
        product.setDeleted(true);
        productRepository.save(product);
    }
}