package com.dogukan.ecommerce.repository;

import com.dogukan.ecommerce.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsByStripeEventId(String stripeEventId);
}
