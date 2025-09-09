package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateDTO {
    @NotBlank(message = "FULL_NAME_INVALID")
    String fullName;

    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^0[0-9]{9}$", message = "PHONE_INVALID")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_INVALID")
    String address;

    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID")
    String password;

    Long roleId;
    Boolean deleted;

}
