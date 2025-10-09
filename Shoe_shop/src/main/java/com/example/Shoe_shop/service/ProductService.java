package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;

import java.util.List;


public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getRelatedProducts(Long id);
    PagedResponse<ProductResponse> getAllProduct(int page, int size, String sortBy, String sortDir);
    PagedResponse<ProductResponse> searchProducts(ProductSearchRequest request,int page, int size, String sortBy, String sortDir);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void softDeleteProduct(Long id); // xoá mềm
}
