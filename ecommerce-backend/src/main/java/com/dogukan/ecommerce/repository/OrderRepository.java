package com.dogukan.ecommerce.repository;

import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findAllByOrderStatusAndCreatedAtBefore(OrderStatus orderStatus, LocalDateTime createdAt);

    Optional<Order> findById(Long id);
}
