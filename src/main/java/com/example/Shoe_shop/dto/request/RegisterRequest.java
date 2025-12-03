package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {

    @NotBlank(message = "USERNAME_INVALID")
    @Size(min = 3, max = 50)
    String username;

    @Email(message = "EMAIL_INVALID")
    String email;

    @NotBlank(message = "PASSWORD_BLANK")
    @Size(min = 6, message = "PASSWORD_INVALID")
    String password;

    @NotBlank(message = "FULL_NAME_INVALID")
    String fullName;

    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^0[0-9]{9}$", message = "PHONE_INVALID")
    String phoneNumber;

    @NotBlank(message = "ADDRESS_INVALID")
    String address;

    List<Long> roleIds;
}
