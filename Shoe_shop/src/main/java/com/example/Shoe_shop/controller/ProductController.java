package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.repository.ProductRepository;
import com.example.Shoe_shop.service.ProductService;
import com.example.Shoe_shop.service.RedisCacheService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;
    final RedisCacheService  redisCacheService;
    final ProductRepository productRepository;
    final ProductMapper productMapper;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .message("Product created successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        String key="product:product-id-"+id;
        ProductResponse cached=redisCacheService.getValue(key,ProductResponse.class);
        if(cached!=null){
            log.info("get product from redis");
            return ApiResponse.<ProductResponse>builder()
                    .result(cached)
                    .build();
        }

        ProductResponse response = productService.getProductById(id);
        redisCacheService.setValueWithTimeout(key,response,15,TimeUnit.SECONDS);
        log.info("get product from database");
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }
    @GetMapping("/{id}/related")
    public ApiResponse<List<ProductResponse>> getRelatedProducts(@PathVariable Long id){
        String key = "related:product:" + id;
        List<Long> relatedIds = redisCacheService.getValue(key, List.class);
        List<ProductResponse> responses;
        if (relatedIds != null && !relatedIds.isEmpty()) {
            responses = productRepository.findAllById(relatedIds).stream()
                    .map(productMapper::toResponse)
                    .toList();
            log.info("get Products from Redis ID list");
        } else {
            responses = productService.getRelatedProducts(id);
            redisCacheService.setValueWithTimeout(key, responses.stream().map(ProductResponse::getId).toList(),10,TimeUnit.MINUTES);
            log.info("get Products from DB");
        }
        return ApiResponse.<List<ProductResponse>>builder()
                .result(responses)
                .build();
    }

    @GetMapping("/get-all-product")
    public ApiResponse<PagedResponse<ProductResponse>>  getAllProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        String key="product:get-all-product-"+page+"-"+size;
        if(redisCacheService.checkExistsKey(key)){
            return ApiResponse.<PagedResponse<ProductResponse>>builder()
                    .result(redisCacheService.getValue(key,PagedResponse.class))
                    .build();
        }
        PagedResponse<ProductResponse> response=productService.getAllProduct(page,size,sortBy,sortDir);
        redisCacheService.setValueWithTimeout(key,response,30,TimeUnit.MINUTES);
        return ApiResponse.<PagedResponse<ProductResponse>>builder()
                .result(response)
                .build();

    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sizeShoe,
            @RequestParam(required = false) String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        ProductSearchRequest request = ProductSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .brandId(brandId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .size(sizeShoe)
                .color(color)
                .build();
        String key = String.format("product:search:%s-%s-%s-%s-%s-%s-%s-%d-%d-%s-%s",
                keyword != null ? keyword : "null",
                categoryId != null ? categoryId : "null",
                brandId != null ? brandId : "null",
                minPrice != null ? minPrice : "null",
                maxPrice != null ? maxPrice : "null",
                sizeShoe != null ? size : "null",
                color != null ? color : "null",
                page,
                size,
                sortBy,
                sortDir
        );
        if(redisCacheService.checkExistsKey(key)){
            log.info("product:search-"+page+"-"+size);
            return ApiResponse.<PagedResponse<ProductResponse>>builder()
                    .result(redisCacheService.getValue(key,PagedResponse.class))
                    .build();
        }
        PagedResponse<ProductResponse> pagedResult =
                productService.searchProducts(request, page, size, sortBy, sortDir);
        redisCacheService.setValueWithTimeout(key,pagedResult,30,TimeUnit.SECONDS);
        log.info("product search lần đầu "+page+"-"+size);
        return ApiResponse.<PagedResponse<ProductResponse>>builder()
                .result(pagedResult)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @RequestBody @Valid ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.softDeleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully")
                .build();
    }
}