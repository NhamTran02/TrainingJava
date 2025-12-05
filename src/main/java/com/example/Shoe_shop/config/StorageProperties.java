package com.example.Shoe_shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "storage.product-images")
public class StorageProperties {
    private String path = "products";
    private String maxFileSize = "20MB";
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "webp");
}