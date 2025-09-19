package com.example.Shoe_shop.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500,"Uncategorized Error"),
    UNAUTHORIZED(401,"You are not logged in please log in"),
    FORBIDDEN(403,"You do not have the right"),
    INVALID_MESSAGE_KEY(400,"Invalid message key"),
    USER_EXISTED(409,"User existed"),
    ROLE_NOT_FOUND(404,"Role doesn't exist"),
    EMAIL_EXISTED(409,"Email existed"),
    PHONE_NUMBER_EXISTED(409,"Phone number existed"),
    USER_NAME_INVALID(400,"User name blank"),
    EMAIL_INVALID(400,"Email không hợp lệ"),
    PASSWORD_INVALID(400,"Mật khẩu phải có ít nhất 6 kí tự"),
    PHONE_INVALID(400,"Số điện thoại không được bỏ trống và phải có 10 số"),
    USER_NOT_FOUND(404,"User not found"),
    PASSWORD_BLANK(400,"Password blank"),
    FULL_NAME_INVALID(400,"Full name blank"),
    ADDRESS_INVALID(400,"Address blank"),
    CATEGORY_NOT_FOUND(400,"Category Not Found"),
    CATEGORY_NAME_INVALID(400,"Category name blank"),
    CATEGORY_HAS_PRODUCTS(409,"Category has products"),
    BRAND_NAME_INVALID(400,"Brand name blank"),
    BRAND_NOT_FOUND(400,"Brand not found"),
    BRAND_HAS_PRODUCTS(400,"Brand has products"),
    PRODUCT_NAME_INVALID(400,"Product name blank"),
    BRAND_ID_INVALID(400,"Brand id blank"),
    CATEGORY_ID_INVALID(400,"Category id blank"),
    SIZE_INVALID(400,"Size blank"),
    COLOR_INVALID(400,"Color blank"),
    REGULAR_PRICE_INVALID(400,"Regular price blank"),
    REGULAR_PRICE_GREATER_THAN_0(400,"original price must be greater than 0"),
    STOCK_INVALID(400,"Stock blank"),
    STOCK_QUANTITY_GREATER_THAN_0(400,"Stock quantity must be greater than 0"),
    PRODUCT_ID_INVALID(400,"Product id blank"),
    IMAGE_URL_INVALID(400,"Image url blank"),
    PRODUCT_NOT_FOUND(404,"Product not found"),
    PRODUCT_ALREADY_DELETED(409,"Product already deleted"),
    USER_ALREADY_DELETED(409,"User already deleted"),
    ALREADY_IN_WISHLIST(409,"Already In Wishlist"),
    WISHLIST_NOT_FOUND(404,"Wishlist not found"),
    VARIANT_ID_REQUIRED(400, "Variant id required"),
    QUANTITY_MIN_1(400,"Quantity must be greater than or equal to 1"),
    CART_EMPTY(404,"Cart is empty"),
    PRODUCT_VARIANT_NOT_FOUND(404,"Product variant not found"),
    STOCK_NOT_ENOUGH(409,"Stock not enough"),
    FILE_EMPTY(400,"File is empty"),
    USER_ID_REQUIRED(400,"User id required"),
    SHIPPING_ADDRESS_REQUIRED(400,"Shipping address required"),
    PAYMENT_METHOD_REQUIRED(400,"Payment method required"),
    OUT_OF_STOCK(400,"Out of stock"),
    TRACKING_NUMBER_BLANK(400,"Tracking number blank"),
    SHIPPING_METHOD_REQUIRED(400,"Shipping method required"),
    ORDER_ID_REQUIRED(400,"Order id required"),
    INVALID_STATUS_TRANSITION(400,"Invalid status transition"),
    ORDER_STATUS_IS_NOT_PENDING(400,"Only orders with PENDING status can be cancelled by users"),
    ORDER_NOT_FOUND(400,"Order not found"),
    ;

    private final int code;
    private final String message;

}
