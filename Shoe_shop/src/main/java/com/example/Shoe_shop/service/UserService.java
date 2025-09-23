package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.UserUpdateDTO;
import com.example.Shoe_shop.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateDTO userUpdateDTO);
    void softDeleteUser(Long id);

}
