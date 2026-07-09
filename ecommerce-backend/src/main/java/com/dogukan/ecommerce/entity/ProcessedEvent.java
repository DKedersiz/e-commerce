package com.dogukan.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_stripe_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEvent {

    @Id
    @Column(name = "stripe_event_id", nullable = false, unique = true)
    private String stripeEventId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;
}