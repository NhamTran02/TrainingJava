package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.CategoryRequest;
import com.example.Shoe_shop.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse  createCategory(CategoryRequest categoryRequest);
    CategoryResponse getCategoryById(Long categoryId);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id,CategoryRequest categoryRequest);
    void deleteCategory(Long id);
}
