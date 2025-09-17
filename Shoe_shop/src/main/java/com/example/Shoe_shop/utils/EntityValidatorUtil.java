package com.example.Shoe_shop.utils;

import com.example.Shoe_shop.entity.*;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityValidatorUtil {
    UserRepository userRepo;
    RoleRepository roleRepo;
    BrandRepository brandRepo;
    CategoryRepository categoryRepo;
    ProductRepository productRepo;
    ProductVariantRepository productVariantRepo;
    ShippingMethodRepository shippingRepo;
    OrderRepository orderRepo;

    public User requireUser(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User requireUserName(String user) {
        return userRepo.findByUsername(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public Role requireRole(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    public Brand requireBrand(Long id) {
        return brandRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND));
    }

    public Category requireCategory(Long id) {
        return categoryRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Product requireProduct(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }
    public ProductVariant requireProductVariant(Long id) {
        return productVariantRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND));
    }
    public ShippingMethod requireShipping(Long id) {
        return shippingRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SHIPPING_METHOD_REQUIRED));
    }
    public Order requireOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_ID_REQUIRED));
    }

}
