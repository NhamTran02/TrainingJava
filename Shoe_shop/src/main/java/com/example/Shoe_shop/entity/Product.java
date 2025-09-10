package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product extends BaseAudit {

    @NotBlank(message = "PRODUCT_NAME_INVALID")
    @Column(nullable = false)
    String name;

    String description;

    @Column(nullable = false)
    Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    List<ProductVariant> variants;

    @OneToMany(mappedBy = "product")
    List<Review> reviews;

    @OneToMany(mappedBy = "product")
    List<Wishlist> wishlists;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images;

}
