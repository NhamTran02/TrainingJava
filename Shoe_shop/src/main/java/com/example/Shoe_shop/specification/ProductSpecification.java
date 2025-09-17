package com.example.Shoe_shop.specification;

import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.entity.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> notDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }

    public static Specification<Product> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Product> hasCategory(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<Product> hasBrand(Long brandId) {
        return (root, query, cb) -> {
            if (brandId == null) return null;
            return cb.equal(root.get("brand").get("id"), brandId);
        };
    }

    public static Specification<Product> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            Join<Product, ProductVariant> variantJoin = root.join("variants");
            List<Predicate> predicates = new ArrayList<>();

            if (minPrice != null) {
                predicates.add(cb.or(
                        cb.greaterThanOrEqualTo(variantJoin.get("salePrice"), minPrice),
                        cb.and(
                                cb.isNull(variantJoin.get("salePrice")),
                                cb.greaterThanOrEqualTo(variantJoin.get("regularPrice"), minPrice)
                        )
                ));
            }

            if (maxPrice != null) {
                predicates.add(cb.or(
                        cb.lessThanOrEqualTo(variantJoin.get("salePrice"), maxPrice),
                        cb.and(
                                cb.isNull(variantJoin.get("salePrice")),
                                cb.lessThanOrEqualTo(variantJoin.get("regularPrice"), maxPrice)
                        )
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> hasSize(String size) {
        return (root, query, cb) -> {
            if (size == null || size.isEmpty()) return null;
            Join<Product, ProductVariant> variantJoin = root.join("variants");
            return cb.equal(variantJoin.get("size"), size);
        };
    }

    public static Specification<Product> hasColor(String color) {
        return (root, query, cb) -> {
            if (color == null || color.isEmpty()) return null;
            Join<Product, ProductVariant> variantJoin = root.join("variants");
            return cb.equal(variantJoin.get("color"), color);
        };
    }

    public static Specification<Product> buildSpecification(
            String keyword,
            Long categoryId,
            Long brandId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String size,
            String color,
            boolean isAdmin
    ){
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        if (!isAdmin) spec = spec.and(notDeleted());
        spec = spec.and(hasKeyword(keyword))
                .and(hasCategory(categoryId))
                .and(hasBrand(brandId))
                .and(hasPriceRange(minPrice, maxPrice))
                .and(hasSize(size))
                .and(hasColor(color));

        return spec;
    }


}
