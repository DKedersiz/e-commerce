package com.dogukan.ecommerce.kafka.consumer;

import com.dogukan.ecommerce.dto.event.OrderCompletedEvent;
import com.dogukan.ecommerce.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailNotificationService emailNotificationService;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000, multiplier = 2.0),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "order-completed", groupId = "ecommerce-group")
    public void consumerOrderCompleted(OrderCompletedEvent orderCompletedEvent) {
        log.info("Kafka'dan yeni bir sipariş event'i alındı: {}", orderCompletedEvent);
        emailNotificationService.sendOrderConfirmation(
                orderCompletedEvent.userEmail(),
                orderCompletedEvent.orderId(),
                orderCompletedEvent.totalAmount()
        );
    }

    @DltHandler
    public void handleDlt(OrderCompletedEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("!!! UYARI !!! Mesaj tüm denemelere rağmen gönderilemedi ve DLT'ye düştü! Düştüğü Topic: {}, Gönderilemeyen Sipariş ID: {}", topic, event.orderId());
    }
}
