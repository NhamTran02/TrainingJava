package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;
import com.example.Shoe_shop.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        AuthenticationResponse res = authService.login(req);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Login successfully!")
                .result(res)
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
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return ApiResponse.<Void>builder()
                .message("Logout successfully!")
                .build();
    }

}
