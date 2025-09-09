package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;
import com.example.Shoe_shop.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ApiResponse.<Void>builder()
                .message("Register successfully!")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@Valid @RequestBody AuthenticationRequest req) {
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully!")
                .result(authService.login(req))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthenticationResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Refresh successfully!")
                .result(authService.refresh(refreshToken))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody Map<String, String> body) {
        authService.logout(body.get("refreshToken"));
        return ApiResponse.<Void>builder()
                .message("Logout successfully!")
                .build();
    }
}
