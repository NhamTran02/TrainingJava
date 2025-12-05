package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.config.StorageProperties;
import com.example.Shoe_shop.exception.StorageException;
import com.example.Shoe_shop.service.MinIOStorageService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MinIOStorageServiceImpl implements MinIOStorageService {
    final MinioClient minioClient;
    final StorageProperties storageProperties;
    
    @Value("${minio.bucket}")
    String bucketName;

//    @PostConstruct
    private void initializeBucket(){
        try{
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Created MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", bucketName, e);
            throw new StorageException("Could not initialize storage bucket:",e);
        }
    }

    @Override
    public String store(MultipartFile file, String path) {
        validateFile(file,storageProperties);
        String objectName = buildObjectName(path, file.getOriginalFilename());
        try (InputStream in = file.getInputStream()){
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(in, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("Stored file: {} to MinIO path: {}", file.getOriginalFilename(), objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Failed to store file: {} to path: {}", file.getOriginalFilename(), objectName, e);
            throw new StorageException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public InputStream retrieve(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to retrieve file from path: {}", path, e);
            throw new StorageException("Failed to retrieve file: " + path, e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .build()
            );
            log.info("Deleted file from MinIO: {}", path);
        } catch (Exception e) {
            log.error("Failed to delete file: {}", path, e);
            throw new StorageException("Failed to delete file: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(path).build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw new StorageException("MinIO stat error", e);
        } catch (Exception e) {
            throw new StorageException("MinIO stat error", e);
        }
    }

    @Override
    public String getPublicUrl(String path) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate public URL for: {}", path, e);
            throw new StorageException("Failed to generate public URL: " + path, e);
        }
    }
     //Validate file trước khi upload
    private void validateFile(MultipartFile file, StorageProperties storageProperties) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.contains("..")) {
            throw new StorageException("Invalid filename: " + filename);
        }
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!storageProperties.getAllowedExtensions().contains(ext)) {
            throw new IllegalArgumentException("File type is not allowed: " + ext);
        }
    }
     //Tạo tên object unique để tránh conflict
    private String buildObjectName(String path, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s/%s%s", path, uniqueId, extension);
    }
}
