package com.example.Shoe_shop.service;

import com.example.Shoe_shop.dto.request.AuthenticationRequest;
import com.example.Shoe_shop.dto.request.RegisterRequest;
import com.example.Shoe_shop.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
    AuthenticationResponse refresh(String refreshToken);
    void logout(String tokenValue);
    void forgotPassword(String email);
}
