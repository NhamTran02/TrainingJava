package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.dto.request.CartItemRequest;
import com.example.Shoe_shop.dto.response.CartItemResponse;
import com.example.Shoe_shop.dto.response.CartResponse;
import com.example.Shoe_shop.entity.ProductVariant;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.CartJdbcRepository;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.CartService;
import com.example.Shoe_shop.utils.EntityValidatorUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class CartServiceImpl implements CartService {
    CartJdbcRepository cartJdbcRepository;
    UserRepository userRepository;
    EntityValidatorUtil entityValidatorUtil;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUserId(Long id) {
        if(!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        Long cartId= cartJdbcRepository.getCartIdByUserId(id);
        List<CartItemResponse> itemResponses=cartJdbcRepository.findCartItems(cartId);
        BigDecimal totalPrice=cartJdbcRepository.getCartTotal(cartId);
        return new CartResponse(cartId,itemResponses,totalPrice);
    }

    @Override
    @Transactional
    public CartResponse addorUpdateItemToCart(CartItemRequest request, Long userId) {
        if(!userRepository.existsById(userId)){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Long cartId= cartJdbcRepository.getCartIdByUserId(userId);
        ProductVariant productVariant=entityValidatorUtil.requireProductVariant(request.getVariantId());

        if (productVariant.getStockQuantity() < request.getQuantity()) {
            throw new AppException(ErrorCode.STOCK_NOT_ENOUGH);
        }
        cartJdbcRepository.addOrUpdateCartItem(cartId, request.getVariantId(),  request.getQuantity());
        List<CartItemResponse> itemResponses=cartJdbcRepository.findCartItems(cartId);
        BigDecimal totalPrice = itemResponses.stream()
                .filter(CartItemResponse::getSelected)
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(cartId,itemResponses,totalPrice);
    }

    @Transactional
    @Override
    public void toggleSelected(Long userId, Long variantId, boolean selected) {
        Long cartId = cartJdbcRepository.getCartIdByUserId(userId);
        cartJdbcRepository.toggleSelectedItem(cartId, variantId, selected);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(CartItemRequest request, Long userId) {
        if(!userRepository.existsById(userId)){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Long cartId= cartJdbcRepository.getCartIdByUserId(userId);
        List<CartItemResponse> itemResponses=cartJdbcRepository.findCartItems(cartId);
        // Kiểm tra item muốn xóa có tồn tại không
        boolean exists= itemResponses.stream().anyMatch(item->item.getVariantId().equals(request.getVariantId()));
        if (!exists) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
        cartJdbcRepository.removeItem(cartId, request.getVariantId());

        BigDecimal totalPrice=cartJdbcRepository.getCartTotal(cartId);
        return new CartResponse(cartId,itemResponses,totalPrice);
    }

    @Override
    @Transactional
    public CartResponse clearCart(Long cartId) {
        cartJdbcRepository.removeCart(cartId);
        List<CartItemResponse> itemResponses=cartJdbcRepository.findCartItems(cartId);
        BigDecimal totalPrice=BigDecimal.ZERO;
        return new  CartResponse(cartId,itemResponses,totalPrice);
    }

}
