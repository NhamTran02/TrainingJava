package com.example.Shoe_shop.service;

import com.example.Shoe_shop.entity.Order;

public interface EmailService {
    void sendVerificationEmail(String email, String code);
    void sendTemporaryPassword(String email, String password);
    void sendOrderConfirmation(Order order);
    void sendReminderEmail(String email, String trackingNumber);

}
