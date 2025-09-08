package com.example.Shoe_shop.repository;

import com.example.Shoe_shop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
