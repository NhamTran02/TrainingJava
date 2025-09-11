package com.example.Shoe_shop.specification;

import com.example.Shoe_shop.entity.Product;
import com.example.Shoe_shop.dto.request.ProductSearchRequest;
import com.example.Shoe_shop.entity.ProductVariant;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> searchProductSpecification(ProductSearchRequest request, boolean isAdmin) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Product, ProductVariant> variantJoin = root.join("variants");

            if (!isAdmin) {
                predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            }

            if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), keyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), keyword)
                ));
            }

            // Lọc theo danh mục
            if (request.getCategoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            // Lọc theo thương hiệu
            if (request.getBrandId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), request.getBrandId()));
            }

            // Lọc theo giá (regularPrice hoặc salePrice nếu có)
            if (request.getMinPrice() != null || request.getMaxPrice() != null) {
                if (request.getMinPrice() != null) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.greaterThanOrEqualTo(variantJoin.get("salePrice"), request.getMinPrice()),
                            criteriaBuilder.and(
                                    criteriaBuilder.isNull(variantJoin.get("salePrice")),
                                    criteriaBuilder.greaterThanOrEqualTo(variantJoin.get("regularPrice"), request.getMinPrice())
                            )
                    ));
                }
                if (request.getMaxPrice() != null) {
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.lessThanOrEqualTo(variantJoin.get("salePrice"), request.getMaxPrice()),
                            criteriaBuilder.and(
                                    criteriaBuilder.isNull(variantJoin.get("salePrice")),
                                    criteriaBuilder.lessThanOrEqualTo(variantJoin.get("regularPrice"), request.getMaxPrice())
                            )
                    ));
                }
            }

            // Lọc theo kích cỡ
            if (request.getSize() != null && !request.getSize().isEmpty()) {
                predicates.add(criteriaBuilder.equal(variantJoin.get("size"), request.getSize()));
            }

            // Lọc theo màu sắc
            if (request.getColor() != null && !request.getColor().isEmpty()) {
                predicates.add(criteriaBuilder.equal(variantJoin.get("color"), request.getColor()));
            }

            // Đảm bảo không có sản phẩm trùng lặp
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
