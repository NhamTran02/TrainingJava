package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {
    List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files, int thumbnailIndex);
    void deleteImage(Long imageId);
    void deleteImages(List<Long> imageIds);
    ProductImageResponse getImageById(Long imageId);
    List<ProductImageResponse> getImagesByProductId(Long productId);
}
