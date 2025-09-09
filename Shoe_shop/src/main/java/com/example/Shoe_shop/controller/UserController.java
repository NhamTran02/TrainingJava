package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.request.UserUpdateDTO;
import com.example.Shoe_shop.dto.response.UserResponse;
import com.example.Shoe_shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser (@PathVariable Long id){
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.getUserById(id));
        return response;
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto){
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.updateUser(id, dto));
        return response;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id){
        userService.softDeleteUser(id);
        return new  ApiResponse<>(200,"User deleted successfully", null);
    }
}
