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
    ;

    private final int code;
    private final String message;

}
