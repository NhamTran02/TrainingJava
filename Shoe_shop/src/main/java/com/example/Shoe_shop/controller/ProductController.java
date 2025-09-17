package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.ProductRequest;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.PagedResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

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
        ProductResponse response = productService.getProductById(id);
        return ApiResponse.<ProductResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<ProductResponse>> searchProducts(
            @RequestBody ProductSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        PagedResponse<ProductResponse> pagedResult =
                productService.searchProducts(request, page, size, sortBy, sortDir);

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