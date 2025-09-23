package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {
    @Query(
            value = """
              SELECT
                  CASE WHEN sale_price IS NOT NULL
                       THEN sale_price
                       ELSE regular_price
                  END
              FROM product_variants
              WHERE id = :variantId
            """,
            nativeQuery = true
    )
    BigDecimal getUnitPrice(Long variantId);

    @Query("SELECT v.stockQuantity FROM ProductVariant v WHERE v.id = :variantId")
    Integer getStockQuantity(Long variantId);


}

