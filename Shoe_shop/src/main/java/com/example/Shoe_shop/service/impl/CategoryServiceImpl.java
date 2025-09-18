package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.CategoryRequest;
import com.example.Shoe_shop.dto.response.CategoryResponse;
import com.example.Shoe_shop.entity.Category;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.CategoryMapper;
import com.example.Shoe_shop.repository.CategoryRepository;
import com.example.Shoe_shop.service.CategoryService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    EntityValidatorUtil entityValidatorUtil;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = categoryMapper.toEntity(categoryRequest);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category=entityValidatorUtil.requireCategory(categoryId);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories=categoryRepository.findAll();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id,CategoryRequest categoryRequest) {
        Category category=entityValidatorUtil.requireCategory(id);
        categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id ) {
        Category category=entityValidatorUtil.requireCategory(id);
        if(!category.getProducts().isEmpty()){
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }
        categoryRepository.delete(category);
    }
}
