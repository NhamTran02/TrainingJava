package com.example.Shoe_shop.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductQueueItem {
    private Long productId;
    private Long categoryId;
}
