package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.entity.Wishlist;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.mapper.ProductMapper;
import com.example.Shoe_shop.repository.WishlistRepository;
import com.example.Shoe_shop.service.WishlistService;
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
public class WishlistServiceImpl implements WishlistService {
    WishlistRepository wishlistRepository;
    EntityValidatorUtil entityValidatorUtil;
    ProductMapper productMapper;

    @Override
    @Transactional
    public void addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.findByUser_IdAndProduct_Id(userId, productId).isPresent()) {
            throw new AppException(ErrorCode.ALREADY_IN_WISHLIST);
        }
        User user = entityValidatorUtil.requireUser(userId);
        Product product = entityValidatorUtil.requireProduct(productId);
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        if (wishlistRepository.findByUser_IdAndProduct_Id(userId, productId).isEmpty()) {
            throw new AppException(ErrorCode.WISHLIST_NOT_FOUND);
        }
        wishlistRepository.deleteByUser_IdAndProduct_Id(userId,productId);

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUser_Id(userId);
        return wishlists.stream()
                .map(wishlist -> productMapper.toResponse(wishlist.getProduct()))
                .toList();
    }
}
