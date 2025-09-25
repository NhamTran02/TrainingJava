package com.example.Shoe_shop.scheduler;

import com.example.Shoe_shop.entity.Payment;
import com.example.Shoe_shop.repository.PaymentRepository;
import com.example.Shoe_shop.service.EmailService;
import com.example.Shoe_shop.service.PaymentService;
import com.example.Shoe_shop.utils.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class PaymentCleanupJob {
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    // Chạy mỗi 1h
    @Scheduled(cron = "0 0 * * * *")
    public void cancelExpiredVnpay() {
        try {
            paymentService.cancelExpiredVnpayPayments();
        } catch (Exception ex) {
            log.error("Failed to cancel expired payments", ex);
        }
    }

    @Scheduled(cron = "0 */5 * * * *") // mỗi 5 phút
    public void reconcile() {
        paymentService.reconcilePendingVnpayPayments();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void remindNearlyExpiredPayments(){
        paymentService.sendReminderForNearlyExpiredPayments();
    }

}