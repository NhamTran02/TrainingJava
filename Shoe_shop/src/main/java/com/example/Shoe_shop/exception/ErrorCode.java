package com.example.Shoe_shop.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized Error"),
    INVALID_MESSAGE_KEY(1000,"Invalid message key"),
    USER_EXISTED(1001,"User existed"),
    ROLE_DOES_NOT_EXTS(1002,"Role doesn't exist"),
    EMAIL_EXISTED(1003,"Email existed"),
    PHONE_NUMBER_EXISTED(1004,"Phone number existed"),
    USERNAME_INVALID(1005,"Username không được để trống"),
    EMAIL_INVALID(1006,"Email không hợp lệ"),
    PASSWORD_INVALID(1007,"Mật khẩu phải có ít nhất 6 kí tự"),
    FULL_NAME_INVALID(1008,"Họ và tên không được để trống"),
    ADDRESS_INVALID(1007,"Địa chỉ không được để trống"),
    PHONE_INVALID(1008,"Số điện thoại không được bỏ trống và phải có 10 số");

    private int code;
    private String message;

}
