package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.response.ProductImageResponse;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.entity.ProductImage;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductImageMapper;
import com.example.Shoe_shop.repository.ProductImageRepository;
import com.example.Shoe_shop.service.MinIOStorageService;
import com.example.Shoe_shop.service.ProductImageService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {
    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    MinIOStorageService minIOStorageService;
    EntityValidatorUtil entityValidatorUtil;

    @Transactional
    @Override
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files, int thumbnailIndex) {
        Product product = entityValidatorUtil.requireProduct(productId);

        List<ProductImageResponse> responses = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            // Upload file to MinIO and get the object path
            String objectPath = minIOStorageService.store(files.get(i), "products/" + productId);
            // URL will be generated on-demand when retrieving images
            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setUrl(minIOStorageService.getPublicUrl(objectPath));
            image.setIsThumbnail(i == thumbnailIndex);
            productImageRepository.save(image);

            log.info("Uploaded image for product {}: {}", productId, objectPath);
            
            // Generate public URL for response only
            ProductImageResponse response = productImageMapper.toResponse(image);
            response.setUrl(minIOStorageService.getPublicUrl(objectPath));
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    @Override
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));

        // Extract object path from URL to delete from MinIO
        String objectPath = extractObjectPathFromUrl(image.getUrl());
        
        try {
            // Delete from MinIO
            if (objectPath != null && minIOStorageService.exists(objectPath)) {
                minIOStorageService.delete(objectPath);
                log.info("Deleted image from MinIO: {}", objectPath);
            }
        } catch (Exception e) {
            log.error("Failed to delete image from MinIO: {}", objectPath, e);
            // Continue to delete from database even if MinIO deletion fails
        }

        // Delete from database
        productImageRepository.delete(image);
        log.info("Deleted image from database: {}", imageId);
    }

    @Transactional
    @Override
    public void deleteImages(List<Long> imageIds) {
        List<ProductImage> images = productImageRepository.findAllById(imageIds);
        
        if (images.isEmpty()) {
            throw new AppException(ErrorCode.IMAGE_NOT_FOUND);
        }

        // Delete from MinIO
        for (ProductImage image : images) {
            String objectPath = extractObjectPathFromUrl(image.getUrl());
            try {
                if (objectPath != null && minIOStorageService.exists(objectPath)) {
                    minIOStorageService.delete(objectPath);
                    log.info("Deleted image from MinIO: {}", objectPath);
                }
            } catch (Exception e) {
                log.error("Failed to delete image from MinIO: {}", objectPath, e);
                // Continue with other deletions
            }
        }

        // Delete from database
        productImageRepository.deleteAll(images);
        log.info("Deleted {} images from database", images.size());
    }

    @Override
    public ProductImageResponse getImageById(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND));
        
        ProductImageResponse response = productImageMapper.toResponse(image);
        // Generate public URL on-demand (lazy loading)
        response.setUrl(minIOStorageService.getPublicUrl(image.getUrl()));
        return response;
    }

    @Override
    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        entityValidatorUtil.requireProduct(productId);
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        
        return images.stream()
                .map(image -> {
                    ProductImageResponse response = productImageMapper.toResponse(image);
                    // Generate public URL on-demand (lazy loading)
                    response.setUrl(minIOStorageService.getPublicUrl(image.getUrl()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    private String extractObjectPathFromUrl(String url) {
        try {
            if (url == null || url.isEmpty()) {
                return null;
            }
            // Remove query parameters
            String urlWithoutParams = url.split("\\?")[0];
            
            // Extract path after bucket name
            // Assuming URL format: http://host:port/bucket/path/to/file
            String[] parts = urlWithoutParams.split("/");
            if (parts.length >= 5) {
                // Skip protocol, empty, host:port, bucket name
                StringBuilder pathBuilder = new StringBuilder();
                for (int i = 4; i < parts.length; i++) {
                    if (i > 4) pathBuilder.append("/");
                    pathBuilder.append(parts[i]);
                }
                return pathBuilder.toString();
            }
            
            return null;
        } catch (Exception e) {
            log.error("Failed to extract object path from URL: {}", url, e);
            return null;
        }
    }
}
