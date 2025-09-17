package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.BrandRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.BrandResponse;
import com.example.Shoe_shop.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ApiResponse.<BrandResponse>builder()
                .message("Brand created successfully")
                .result(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable Long id) {
        BrandResponse response = brandService.getBrandById(id);
        return ApiResponse.<BrandResponse>builder()
                .result(response)
                .build();
    }

    @GetMapping("")
    public ApiResponse<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> list = brandService.getAllBrands();
        return ApiResponse.<List<BrandResponse>>builder()
                .result(list)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable Long id, @RequestBody @Valid BrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ApiResponse.<BrandResponse>builder()
                .message("Brand updated successfully")
                .result(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ApiResponse.<Void>builder()
                .message("Brand deleted successfully")
                .build();
    }
}
