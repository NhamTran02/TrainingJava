package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_images")
public class ProductImage extends BaseAudit {

    @NotBlank(message = "PRODUCT_ID_INVALID")
    @Column(nullable = false,name = "image_url")
    String url;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "is_thumbnail")
    Boolean isThumbnail = false; // dùng để đánh dấu ảnh chính
}
