package com.example.Shoe_shop.entity;

import com.example.Shoe_shop.entity.base.BaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "reviews")
public class Review extends BaseId {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    Order order;

    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating phải từ 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    Integer rating;

    String comment;

    @Column(name = "is_edited")
    Boolean isEdited = false;

    @Column(name = "edit_count")
    Integer editCount = 0;

}
