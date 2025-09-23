package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Payment;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTxnRef(String txnRef);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.txnRef = :txnRef")
    Optional<Payment> findByTxnRefForUpdate(@Param("txnRef") String txnRef);
    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime before);
    Optional<Payment> findTopByOrder_IdOrderByCreatedAtDesc(Long orderId);

    List<Payment> findByOrOrder_Id(Long orderId);
}
