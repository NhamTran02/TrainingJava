package com.example.Shoe_shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDTO {

    @NotBlank(message = "USERNAME_INVALID")
    @Size(min = 3, max = 50)
    private String username;

    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank
    @Size(min = 6, message = "PASSWORD_INVALID")
    private String password;

    @NotBlank(message = "FULL_NAME_INVALID")
    private String fullName;

    @NotBlank(message = "PHONE_INVALID")
    @Pattern(regexp = "^0[0-9]{9}$", message = "PHONE_INVALID")
    private String phoneNumber;

    @NotBlank(message = "ADDRESS_INVALID")
    private String address;

    private Long roleId;
}
