package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.ProductResponse;
import com.example.Shoe_shop.entity.Wishlist;
import com.example.Shoe_shop.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/{userId}/{productId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<Void> addWishlist(@PathVariable Long userId, @PathVariable Long productId){
        wishlistService.addToWishlist(userId,productId);
        return ApiResponse.<Void>builder()
                .message("Product added wishlist successfully!")
                .build();
    }

    @DeleteMapping("/{userId}/{productId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<Void> deleteWishlist(@PathVariable Long userId, @PathVariable Long productId){
        wishlistService.removeFromWishlist(userId,productId);
        return ApiResponse.<Void>builder()
                .message("Product deleted wishlist successfully!")
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<List<ProductResponse>> getWishlist(@PathVariable Long userId){
        List<ProductResponse> wishlist= wishlistService.getWishlist(userId);
        return ApiResponse.<List<ProductResponse>>builder()
                .result(wishlist)
                .build();
    }

}
