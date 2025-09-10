package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("")
    public ApiResponse<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .message("Product created successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("")
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResponse.<List<ProductResponse>>builder()
                .result(products)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable Long id,
                                                      @RequestBody ProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Product deleted successfully")
                .build();
    }
}