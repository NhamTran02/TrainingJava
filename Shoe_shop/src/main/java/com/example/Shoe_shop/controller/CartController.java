package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.CartItemRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.CartResponse;
import com.example.Shoe_shop.service.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id ")
    public ApiResponse<CartResponse> getCartByUserId(@PathVariable Long userId){
        CartResponse cartResponse=cartService.getCartByUserId(userId);
        System.out.println("getCartByUserId:"+cartResponse);

        if(cartResponse==null){
            return ApiResponse.<CartResponse>builder()
                    .message("Cart is empty")
                    .build();
        }
        return ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .build();
    }

    @GetMapping("/nqluong/conflict")
    public ApiResponse<String> testConflict(){
        return ApiResponse.<String>builder()
                .result("No Conflict")
                .build();
    }

    @PostMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<CartResponse> addItemToCart(@PathVariable Long userId,@RequestBody @Valid CartItemRequest cartItemRequest){
        CartResponse cartResponse=cartService.addorUpdateItemToCart(cartItemRequest,userId);
        return ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .build();
    }

    @PutMapping("/{userId}/{variantId}")
    @PreAuthorize("#userId == authentication.principal.user.id")
    public ApiResponse<Void> toggleSelected(
            @PathVariable Long userId,
            @PathVariable Long variantId,
            @RequestParam boolean selected) {
        cartService.toggleSelected(userId, variantId, selected);
        return ApiResponse.<Void>builder()
                .message("Success")
                .build();
    }

    @GetMapping("/nqluong/test-exception")
    public ApiResponse<String> testException(){
        if(true){
            throw new RuntimeException("Test exception handling");
        }
        return ApiResponse.<String>builder()
                .result("No Exception")
                .build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long userId,@RequestBody CartItemRequest cartItemRequest){
        CartResponse cartResponse=cartService.removeItemFromCart(cartItemRequest,userId);
        return ApiResponse.<CartResponse>builder()
                .message("Remove item successfully")
                .result(cartResponse)
                .build();
    }

    @DeleteMapping("/clear/{cartId}")
    @PreAuthorize("@cartSecurity.hasAccess(authentication, #cartId)")
    public ApiResponse<CartResponse> clearCart(@PathVariable Long cartId){
        CartResponse cartResponse=cartService.clearCart(cartId);
        return  ApiResponse.<CartResponse>builder()
                .message("Clear cart successfully")
                .result(cartResponse)
                .build();
    }

}
