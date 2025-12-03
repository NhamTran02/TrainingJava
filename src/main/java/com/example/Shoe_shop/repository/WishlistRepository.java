package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser_IdAndProduct_Id(Long userId, Long productId);
    List<Wishlist> findByUser_Id(Long userId);

    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
}
