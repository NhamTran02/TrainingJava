package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.ShippingMethod;
import com.example.Shoe_shop.utils.enums.ShippingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod,Long> {
    Optional<ShippingMethod> findByMethodName(ShippingType methodName);
}
