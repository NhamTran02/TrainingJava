package com.example.Shoe_shop.service;

public interface EmailService {
    void sendVerificationEmail(String email, String code);
    void sendTemporaryPassword(String email, String password);
}
