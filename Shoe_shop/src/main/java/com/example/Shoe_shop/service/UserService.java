package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.UserCreateDTO;
import com.example.Shoe_shop.entity.User;

import java.util.List;

public interface UserService {
    User createUser(UserCreateDTO user);
    List<User> getAllUsers();
    User getUserById(Long id);

}
