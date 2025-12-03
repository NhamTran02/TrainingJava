package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.ChangePasswordRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.request.UserUpdateDTO;
import com.example.Shoe_shop.dto.response.UserResponse;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<UserResponse> getUser (@PathVariable Long id){
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.getUserById(id));
        return response;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO dto){
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setResult(userService.updateUser(id, dto));
        return response;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id){
        userService.softDeleteUser(id);
        return new  ApiResponse<>(200,"User deleted successfully", null);
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changeUser(@RequestBody @Valid ChangePasswordRequest request,
                                        @AuthenticationPrincipal UserDetails currentUser){
        userService.changePassword(currentUser.getUsername(),request);
        return ApiResponse.<Void>builder()
                .message("Change password successfully")
                .build();
    }

}
