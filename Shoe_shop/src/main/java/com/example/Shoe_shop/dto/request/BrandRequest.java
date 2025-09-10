package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequest {
    @NotBlank(message = "BRAND_NAME_INVALID")
    private String name;
}
