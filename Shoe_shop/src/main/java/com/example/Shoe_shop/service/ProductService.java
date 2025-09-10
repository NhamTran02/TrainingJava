package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    Page<ProductResponse> searchProducts(ProductRequest request, Pageable pageable);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id); // xoá mềm
}
