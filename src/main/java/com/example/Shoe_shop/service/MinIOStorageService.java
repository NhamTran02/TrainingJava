package com.example.Shoe_shop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinIOStorageService {
    //Lưu file và trả về URL có thể truy cập
    String store(MultipartFile file, String path);
    //Lấy file dưới dạng InputStream
    InputStream retrieve(String path);
    //Xóa file
    void delete(String path);
    //Kiểm tra file có tồn tại không
    boolean exists(String path);
    //Lấy public URL của file (cho external access)
    String getPublicUrl(String path);
}
