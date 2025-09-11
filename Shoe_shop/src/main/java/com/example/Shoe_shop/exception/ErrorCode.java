package com.example.Shoe_shop.exception;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized Error"),
    INVALID_MESSAGE_KEY(1000,"Invalid message key"),
    USER_EXISTED(1001,"User existed"),
    ROLE_NOT_FOUND(1002,"Role doesn't exist"),
    EMAIL_EXISTED(1003,"Email existed"),
    PHONE_NUMBER_EXISTED(1004,"Phone number existed"),
    USER_NAME_INVALID(1005,"User name blank"),
    EMAIL_INVALID(1006,"Email không hợp lệ"),
    PASSWORD_INVALID(1007,"Mật khẩu phải có ít nhất 6 kí tự"),
    PHONE_INVALID(1008,"Số điện thoại không được bỏ trống và phải có 10 số"),
    USER_NOT_FOUND(1009,"User not found"),
    PASSWORD_BLANK(1010,"Password blank"),
    FULL_NAME_INVALID(1011,"Full name blank"),
    ADDRESS_INVALID(1012,"Address blank"),
    UNAUTHENTICATED(1013,"Unauthenticated"),
    CATEGORY_NOT_FOUND(1014,"Category Not Found"),
    CATEGORY_NAME_INVALID(1015,"Category name blank"),
    CATEGORY_HAS_PRODUCTS(1016,"Category has products"),
    BRAND_NAME_INVALID(1017,"Brand name blank"),
    BRAND_NOT_FOUND(1018,"Brand not found"),
    BRAND_HAS_PRODUCTS(1019,"Brand has products"),
    PRODUCT_NAME_INVALID(1020,"Product name blank"),
    BRAND_ID_INVALID(1021,"Brand id blank"),
    CATEGORY_ID_INVALID(1022,"Category id blank"),
    SIZE_INVALID(1023,"Size blank"),
    COLOR_INVALID(1024,"Color blank"),
    REGULAR_PRICE_INVALID(1025,"Regular price blank"),
    REGULAR_PRICE_GREATER_THAN_0(1026,"original price must be greater than 0"),
    STOCK_INVALID(1027,"Stock blank"),
    STOCK_QUANTITY_GREATER_THAN_0(1028,"Stock quantity must be greater than 0"),
    PRODUCT_ID_INVALID(1029,"Product id blank"),
    IMAGE_URL_INVALID(1030,"Image url blank"),
    PRODUCT_NOT_FOUND(1031,"Product not found"),
    PRODUCT_ALREADY_DELETED(1032,"Product already deleted"),
    USER_ALREADY_DELETED(1033,"User already deleted"),
    ;

    private final int code;
    private final String message;

}
