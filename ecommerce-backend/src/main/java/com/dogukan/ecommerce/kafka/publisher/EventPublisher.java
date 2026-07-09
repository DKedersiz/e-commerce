package com.dogukan.ecommerce.kafka.publisher;

public interface EventPublisher {
    void publish(String topic, Object event);
}
