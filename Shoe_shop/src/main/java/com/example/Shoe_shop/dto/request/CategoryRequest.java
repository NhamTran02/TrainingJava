package com.example.Shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "CATEGORY_NAME_INVALID")
    private String name;
}
