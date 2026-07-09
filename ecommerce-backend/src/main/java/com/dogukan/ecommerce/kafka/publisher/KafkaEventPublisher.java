package com.dogukan.ecommerce.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(String topic, Object event) {
        try {
            kafkaTemplate.send(topic, event);
            log.info("Mesaj [{}] kuyruğuna başarıyla gönderildi: {}", topic, event.getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Mesaj gönderilirken Kafka'da hata oluştu! Topic: {}", topic, e);
        }
    }
}
