package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,JpaSpecificationExecutor<Product> {
    List<Product> findByDeletedFalse();
    @NotNull Page<Product> findAll(Specification<Product> spec, @NotNull Pageable pageable);
}
