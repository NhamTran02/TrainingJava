package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.*;
import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductImageMapper;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.mapper.ProductVariantMapper;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.service.RedisCacheService;
import com.example.Shoe_shop.specification.ProductSpecification;
import com.example.Shoe_shop.utils.CheckRole;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    ProductVariantMapper productVariantMapper;
    ProductImageMapper productImageMapper;
    EntityValidatorUtil entityValidatorUtil;
    RedisCacheService redisCacheService;
    static final String QUEUE_KEY = "product:queue";
    static final String RELATED_CACHE_PREFIX = "related:product:";


    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Brand brand = entityValidatorUtil.requireBrand(request.getBrandId());
        Category category = entityValidatorUtil.requireCategory(request.getCategoryId());

        Product product = productMapper.toEntity(request);
        product.setBrand(brand);
        product.setCategory(category);
        productRepository.save(product);
        ProductQueueItem item =ProductQueueItem.builder()
                .categoryId(category.getId())
                .productId(product.getId())
                .build();
        redisCacheService.lPush(QUEUE_KEY, item);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = entityValidatorUtil.requireProduct(id);
        if(!CheckRole.isAdmin() && product.getDeleted()){
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        String relatedKey=RELATED_CACHE_PREFIX + id;
        List<Long> relatedIds=redisCacheService.getValue(relatedKey,List.class);
        if (relatedIds == null || relatedIds.isEmpty()) {
            ProductQueueItem item=ProductQueueItem.builder()
                    .productId(product.getId())
                    .categoryId(product.getCategory().getId())
                    .build();
            redisCacheService.lPush(QUEUE_KEY, item);
            log.info("Pushed product {} to queue for related processing", id);
        }

        ProductResponse response=productMapper.toResponse(product);
        response.setVariants(productVariantMapper.toResponseList(product.getVariants()));
        response.setImages(productImageMapper.toResponseList(product.getImages()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductByIdInternal(Long id) {
        Product product = entityValidatorUtil.requireProduct(id);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductByCategoryId(Long categoryId, int page, int size, String sortBy, String sortDir) {
        // Kiểm tra category tồn tại
        entityValidatorUtil.requireCategory(categoryId);

        // Tạo pageable với sort
        Pageable pageable = PageRequest.of(page, Math.min(size, 20),
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        // Lấy danh sách sản phẩm theo category với phân trang và sort
        Page<Product> products = productRepository.findAllByCategory_Id(categoryId, pageable);

        List<ProductResponse> content = products.stream().map(p -> {
            ProductResponse r = productMapper.toResponse(p);
            r.setVariants(productVariantMapper.toResponseList(p.getVariants()));
            r.setImages(productImageMapper.toResponseList(p.getImages()));
            return r;
        }).toList();

        // Trả về PagedResponse
        return new PagedResponse<>(
                products.getNumber(),
                products.getSize(),
                products.getTotalElements(),
                products.getTotalPages(),
                content
        );
    }


    @Override
    public List<ProductResponse> getRelatedProducts(Long id) {
        Product product = entityValidatorUtil.requireProduct(id);
        Long categoryId = product.getCategory().getId();
        List<Product> relatedProducts = productRepository.findTop20ByCategory_Id(categoryId);
        List<ProductResponse> responses = relatedProducts.stream()
                .map(p -> {
                    ProductResponse r = productMapper.toResponse(p);
                    r.setVariants(productVariantMapper.toResponseList(p.getVariants()));
                    r.setImages(productImageMapper.toResponseList(p.getImages()));
                    return r;
                })
                .toList();
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProduct(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.fromString(sortDir), sortBy));

        Page<Product> products = productRepository.findAllProduct(pageable);

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
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(
            ProductSearchRequest request,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        log.info("Search params: keyword={}, categoryId={}, brandId={}, minPrice={}, maxPrice={}, size={}, color={}",
                request.getKeyword(), request.getCategoryId(), request.getBrandId(),
                request.getMinPrice(), request.getMaxPrice(), request.getSize(), request.getColor());

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
        log.info("Found {} products", products.getTotalElements());
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

        redisCacheService.deleteKey("related:product:" + id);
        ProductQueueItem item =ProductQueueItem.builder()
                .categoryId(category.getId())
                .productId(product.getId())
                .build();
        redisCacheService.lPush(QUEUE_KEY, item);

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
        redisCacheService.deleteKey(RELATED_CACHE_PREFIX + id);

    }
}