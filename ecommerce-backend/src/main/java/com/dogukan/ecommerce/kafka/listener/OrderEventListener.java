package com.dogukan.ecommerce.kafka.listener;

import com.dogukan.ecommerce.dto.event.OrderCompletedEvent;
import com.dogukan.ecommerce.kafka.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final EventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompletedEvent(OrderCompletedEvent orderCompletedEvent) {
        log.info("Veritabanı transaction'ı başarıyla commit edildi. Event kafkaya gönderiliyor");
        eventPublisher.publish("order-completed",orderCompletedEvent);
    }
}
