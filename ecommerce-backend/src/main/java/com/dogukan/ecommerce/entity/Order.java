package com.dogukan.ecommerce.entity;

import com.dogukan.ecommerce.entity.base.BaseEntity;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.dogukan.ecommerce.util.enums.PaymentStatus.FAILED;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setOrder(this);
    }

    public void transitionTo(OrderStatus newStatus) {
        if (!isValidTransition(this.orderStatus, newStatus)) {
            throw new BusinessException(ErrorType.INVALID_STATUS_TRANSITION);
        }

        this.orderStatus = newStatus;
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        if (current == null || next == null) {
            return false;
        }

        return switch (current) {
            case PENDING -> next == OrderStatus.COMPLETED || next == OrderStatus.FAILED || next == OrderStatus.CANCELLED;
            case FAILED -> next == OrderStatus.PENDING;
            default -> false;
        };
    }
}
