package com.example.Shoe_shop.specification;

import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.entity.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
    public static Specification<Product> hasVariantAttributes(
            String size, String color, BigDecimal minPrice, BigDecimal maxPrice
    ) {
        return (root, query, cb) -> {
            boolean hasAnyFilter = (size != null && !size.isEmpty()) ||
                    (color != null && !color.isEmpty()) ||
                    minPrice != null ||
                    maxPrice != null;

            if (!hasAnyFilter) {
                return null;
            }

            Join<Product, ProductVariant> variantJoin = root.join("variants", JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();

            if (size != null && !size.isEmpty()) {
                predicates.add(cb.equal(variantJoin.get("size"), size));
            }
            if (color != null && !color.isEmpty()) {
                predicates.add(cb.equal(variantJoin.get("color"), color));
            }
            if (minPrice != null) {
                predicates.add(cb.or(
                        cb.greaterThanOrEqualTo(variantJoin.get("salePrice"), minPrice),
                        cb.and(cb.isNull(variantJoin.get("salePrice")),
                                cb.greaterThanOrEqualTo(variantJoin.get("regularPrice"), minPrice))
                ));
            }
            if (maxPrice != null) {
                predicates.add(cb.or(
                        cb.lessThanOrEqualTo(variantJoin.get("salePrice"), maxPrice),
                        cb.and(cb.isNull(variantJoin.get("salePrice")),
                                cb.lessThanOrEqualTo(variantJoin.get("regularPrice"), maxPrice))
                ));
            }

            query.distinct(true);
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
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
                .and(hasVariantAttributes(size, color, minPrice, maxPrice));
        return spec;
    }
}
