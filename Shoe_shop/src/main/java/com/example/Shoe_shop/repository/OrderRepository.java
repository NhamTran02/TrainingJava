package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.dto.response.projection.OrderSummaryProjection;
import com.example.Shoe_shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query(value = "SELECT id, total_amount, status, DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') AS createdAt " +
            "FROM orders WHERE user_id = :userId ORDER BY created_at DESC",
            nativeQuery = true)
    List<OrderSummaryProjection> findOrderByUserId(Long userId);

    boolean existsByTrackingNumber(String trackingNumber);

}
