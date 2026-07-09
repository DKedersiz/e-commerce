package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendOrderConfirmation(String email, Long orderId, BigDecimal totalAmount) {
        log.info("E-posta gönderim işlemi başlatıldı. Kime: {}", email);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@ecommerce.com");
            message.setTo(email);
            message.setSubject("Siparişiniz Başarıyla Alındı! #" + orderId);
            message.setText("Değerli müşterimiz,\n\n" +
                    totalAmount + " TL tutarındaki #" + orderId + " nolu siparişiniz başarıyla alınmıştır.\n" +
                    "Bizi tercih ettiğiniz için teşekkür ederiz.");

            javaMailSender.send(message);
            log.info("E-posta başarıyla Mailhog'a gönderildi. Sipariş ID: {}", orderId);
        } catch (Exception e) {
            log.error("E-posta gönderilirken bir hata oluştu! Sipariş ID: {}", orderId, e);
            throw new BusinessException(ErrorType.EMAIL_SENDING_FAILED);
        }
    }
}
