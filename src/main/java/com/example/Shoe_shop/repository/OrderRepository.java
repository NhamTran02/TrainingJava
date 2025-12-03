package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.dto.response.projection.OrderSummaryProjection;
import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.utils.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query(value = "SELECT id, total_amount, status,shipping_fee,tracking_number, created_at " +
            "FROM orders WHERE user_id = :userId ORDER BY created_at DESC",
            nativeQuery = true)
    List<OrderSummaryProjection> findOrderByUserId(Long userId);
    boolean existsByTrackingNumber(String trackingNumber);
    Page<Order> findAllByStatus(@Param("status") OrderStatus status,Pageable pageable);
    Page<Order> findAllByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    Optional<Order> findByTrackingNumber(String trackingNumber);
}
