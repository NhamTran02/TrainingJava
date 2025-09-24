package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {
    final JavaMailSender mailSender;
    String backendUrl = "http://localhost:8080/api";
    @Value("${spring.mail.username}")
    String emailFrom;


    @Override
    public void sendVerificationEmail(String email, String code) {
        String verifyLink=backendUrl+"/auth/verify?code="+code;
        String subject= "Xác minh tài khoản Shoe Shop";
        String content = "Chào bạn,\n\n"
                + "Vui lòng xác minh tài khoản bằng cách click vào link sau:\n"
                + verifyLink + "\n\n"
                + "Cảm ơn!";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(emailFrom);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    @Override
    public void sendTemporaryPassword(String email, String password) {
        String subject="Mật khẩu tạm thời - Shoe Shop";
        String content="Mật khẩu tạm thời của bạn là :"+ password +
                "\nVui lòng đăng nhập và đổi lại mật khẩu ngay.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(emailFrom);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
