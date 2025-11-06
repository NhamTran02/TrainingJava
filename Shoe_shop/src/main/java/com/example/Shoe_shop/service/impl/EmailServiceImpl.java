package com.example.Shoe_shop.service.impl;

import com.example.Shoe_shop.entity.Order;
import com.example.Shoe_shop.repository.PaymentRepository;
import com.example.Shoe_shop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {
    final JavaMailSender mailSender;
    final SpringTemplateEngine templateEngine;
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

    @Override
    public void sendOrderConfirmation(Order order) {
        Context context = new Context();
        context.setVariable("orderId",order.getId());
        context.setVariable("customerName",order.getUser().getUsername());
        context.setVariable("orderItems",order.getOrderDetails());
        context.setVariable("orderTotal",order.getTotalAmount());
        context.setVariable("shippingAddress",order.getShippingAddress());

        String html=templateEngine.process("order-confirmation", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailFrom);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Xác nhận đơn hàng #" + order.getTrackingNumber());
            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendReminderEmail(String email, String trackingNumber) {
        String subject="Nhắc nhở thanh toán đơn hàng";
        String content="Đơn hàng của bạn (Mã đơn: " + trackingNumber + ") " +
                "vẫn chưa được thanh toán. Vui lòng thanh toán trước khi đơn hàng bị huỷ.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(emailFrom);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }


}
