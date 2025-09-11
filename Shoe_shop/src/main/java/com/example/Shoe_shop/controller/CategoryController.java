package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.CategoryRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.CategoryResponse;
import com.example.Shoe_shop.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        categoryService.createCategory(categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category created successfully")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryResponse)
                .build();
    }

    @GetMapping("")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories=categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categories)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id,@RequestBody @Valid CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryResponse)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<CategoryResponse>builder()
                .message("Category deleted successfully")
                .build();
    }

}
