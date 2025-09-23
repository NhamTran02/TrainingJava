package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.PurchaseOrderItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem,Long> {
    @Query("""
        SELECT poi.unitCost
        FROM PurchaseOrderItem poi
        WHERE poi.variant.id= :variantId
        ORDER BY poi.purchaseOrder.orderDate DESC
    """)
    Optional<PurchaseOrderItem> findTopUnitCostByVariantId(@Param("variantId") Long variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT poi FROM PurchaseOrderItem poi
        where poi.variant.id= :variantId
        AND poi.remainingQty > :quantity
        order by poi.purchaseOrder.orderDate ASC
    """)
    List<PurchaseOrderItem> findAllByVariantId(@Param("variantId") Long variantId, @Param("quantity")  Integer quantity);


}
