package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import com.dogukan.ecommerce.dto.response.PaymentResult;
import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.OrderItem;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.repository.OrderRepository;
import com.dogukan.ecommerce.repository.PaymentRepository;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.dto.event.OrderCompletedEvent;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import com.dogukan.ecommerce.util.enums.PaymentStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockPaymentServiceImplTest {

    @Spy
    @InjectMocks
    private MockPaymentServiceImpl paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void when_payOrder_success_then_orderCompleted() {
        Long orderId = 1L;

        PaymentRequest request = PaymentRequest.builder()
                .cardNumber("1234567890121234")
                .cardHolderName("Test User")
                .build();

        Order order = Instancio.of(Order.class)
                .set(field(Order::getOrderStatus), OrderStatus.PENDING)
                .set(field(Order::getTotalAmount), BigDecimal.valueOf(500))
                .set(field(Order::getItems), new ArrayList<>())
                .create();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doReturn(new PaymentResult(true, null))
                .when(paymentService).processPayment(any(), any());
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = paymentService.payOrder(orderId, request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS.name(), response.getStatus());
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        verify(paymentRepository).save(argThat(payment ->
                payment.getStatus() == PaymentStatus.SUCCESS
        ));
        verify(eventPublisher, times(1)).publishEvent(any(OrderCompletedEvent.class));
    }

    @Test
    void when_payOrder_fails_then_stocksRestored() {
        Long orderId = 1L;

        PaymentRequest request = PaymentRequest.builder()
                .cardNumber("1234567890120000")
                .cardHolderName("Test User")
                .build();

        Product product = Instancio.of(Product.class)
                .set(field(Product::getId), 10L)
                .create();

        OrderItem orderItem = Instancio.of(OrderItem.class)
                .set(field(OrderItem::getProduct), product)
                .set(field(OrderItem::getQuantity), 3)
                .create();

        Order order = Instancio.of(Order.class)
                .set(field(Order::getOrderStatus), OrderStatus.PENDING)
                .set(field(Order::getTotalAmount), BigDecimal.valueOf(300))
                .set(field(Order::getItems), List.of(orderItem))
                .create();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doReturn(new PaymentResult(false, "Insufficent funds (MOCK)"))
                .when(paymentService).processPayment(any(), any());
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentResponse response = paymentService.payOrder(orderId, request);

        assertEquals(PaymentStatus.FAILED.name(), response.getStatus());
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        verify(productService).restoreStockBulk(argThat(map ->
                map.containsKey(product.getId()) && map.get(product.getId()).equals(orderItem.getQuantity())
        ));
        verify(paymentRepository).save(argThat(payment ->
                payment.getStatus() == PaymentStatus.FAILED &&
                        payment.getFailureReason().equals("Insufficent funds (MOCK)")
        ));
    }

    @Test
    void when_payOrder_orderNotFound_then_throwException() {
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> paymentService.payOrder(99L, Instancio.create(PaymentRequest.class)));

        assertEquals(ErrorType.ORDER_NOT_FOUND, ex.getErrorType());
        verify(paymentRepository, never()).save(any());
        verify(productService, never()).restoreStockBulk(anyMap());
    }
}