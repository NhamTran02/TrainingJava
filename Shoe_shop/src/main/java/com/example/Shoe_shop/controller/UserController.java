package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.ApiResponse;
import com.example.Shoe_shop.dto.UserCreateDTO;
import com.example.Shoe_shop.entity.User;
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

    @PostMapping("")
    public ApiResponse<User> addUser(@RequestBody @Valid UserCreateDTO dto){
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.createUser(dto));
        return response;
    }

    @GetMapping("/{id}")
    public User getUser (@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
