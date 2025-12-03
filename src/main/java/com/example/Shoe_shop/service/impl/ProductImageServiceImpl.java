package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.response.ProductImageResponse;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.entity.ProductImage;
import com.example.Shoe_shop.mapper.ProductImageMapper;
import com.example.Shoe_shop.repository.ProductImageRepository;
import com.example.Shoe_shop.service.FileStorageService;
import com.example.Shoe_shop.service.ProductImageService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ProductImageServiceImpl implements ProductImageService {
    ProductImageRepository productImageRepository;
    ProductImageMapper productImageMapper;
    FileStorageService fileStorageService;
    EntityValidatorUtil entityValidatorUtil;

    @Transactional
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files, int thumbnailIndex) {
        Product product = entityValidatorUtil.requireProduct(productId);

        List<ProductImageResponse> responses = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String url = fileStorageService.storeFile(files.get(i));

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setUrl(url);
            image.setIsThumbnail(i == thumbnailIndex);
            productImageRepository.save(image);

            responses.add(productImageMapper.toResponse(image));
        }
        return responses;
    }
}
