package com.example.Shoe_shop.controller;

import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.ApiResponse;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;
import com.example.Shoe_shop.entity.User;
import com.example.Shoe_shop.exception.AppException;
import com.example.Shoe_shop.exception.ErrorCode;
import com.example.Shoe_shop.repository.UserRepository;
import com.example.Shoe_shop.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserRepository userRepository;

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

    @GetMapping("/verify")
    public ApiResponse<AuthenticationResponse> verifyEmail(@RequestParam String code) {
        User user= userRepository.findByVerificationCode(code)
                .orElseThrow(()-> new AppException(ErrorCode.VERIFYCODE_NOT_FOUND));
        user.setVerified(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        return ApiResponse.<AuthenticationResponse>builder()
                .message("Verify successfully!")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null && !refreshToken.isEmpty()) {
            authService.logout(refreshToken);
        }
        return ApiResponse.<Void>builder()
                .message("Logout successfully!")
                .build();
    }
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ApiResponse.<Void>builder()
                .message("Mật khẩu tạm thời đã được gửi tới email của bạn.")
                .build();
    }

}
