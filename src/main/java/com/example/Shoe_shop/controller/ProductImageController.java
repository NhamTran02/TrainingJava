package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.ProductImageResponse;
import com.example.Shoe_shop.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {
    final ProductImageService productImageService;

    @PostMapping(value = "/{productId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<ProductImageResponse>> uploadImages(
            @PathVariable Long productId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(required = false, defaultValue = "0") int thumbnailIndex
    ) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.uploadImages(productId, files, thumbnailIndex))
                .message("Images uploaded successfully")
                .build();
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<ProductImageResponse>> getImagesByProductId(@PathVariable Long productId) {
        return ApiResponse.<List<ProductImageResponse>>builder()
                .result(productImageService.getImagesByProductId(productId))
                .message("Get images successfully")
                .build();
    }

    @GetMapping("/{imageId}")
    public ApiResponse<ProductImageResponse> getImageById(@PathVariable Long imageId) {
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.getImageById(imageId))
                .message("Get image successfully")
                .build();
    }

    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ApiResponse.<Void>builder()
                .message("Image deleted successfully")
                .build();
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteImages(@RequestBody List<Long> imageIds) {
        productImageService.deleteImages(imageIds);
        return ApiResponse.<Void>builder()
                .message("Images deleted successfully")
                .build();
    }
}
