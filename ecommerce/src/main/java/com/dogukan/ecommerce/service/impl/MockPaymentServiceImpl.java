package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.PaymentRequest;
import com.dogukan.ecommerce.dto.response.PaymentResponse;
import com.dogukan.ecommerce.dto.response.PaymentResult;
import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.OrderItem;
import com.dogukan.ecommerce.entity.Payment;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.repository.OrderRepository;
import com.dogukan.ecommerce.repository.PaymentRepository;
import com.dogukan.ecommerce.service.MockPaymentService;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import com.dogukan.ecommerce.util.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockPaymentServiceImpl implements MockPaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductService productService;


    @Override
    public PaymentResult processPayment(PaymentRequest paymentRequest, BigDecimal amount) {
        log.info("Payment process started. Amount: {} TL, Card Holder: {}", amount, paymentRequest.getCardHolderName());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if(paymentRequest.getCardNumber() != null && paymentRequest.getCardNumber().endsWith("0000")) {
            log.warn("Payment denied! Reason: Insufficent funds (MOCK)");
            return new PaymentResult(false, "Insufficent funds (MOCK)");
        }

        log.info("Ödeme BAŞARILI! Tutar: {} TL çekildi.", amount);
        return new PaymentResult(true, null);
    }

    @Override
    public PaymentResponse payOrder(Long orderId, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorType.ORDER_NOT_FOUND));

        PaymentResult result = processPayment(request,order.getTotalAmount());

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .cardLastFour(request.getCardNumber().substring(request.getCardNumber().length() - 4))
                .build();

        if(result.isSuccess()) {
            handleSuccess(order,payment);
        } else {
            handleFailure(order, payment, result.failureReason());
        }

        paymentRepository.save(payment);
        return new PaymentResponse(payment.getStatus().name(),payment.getFailureReason());
    }

    private void handleSuccess(Order order, Payment payment) {
        order.transitionTo(OrderStatus.COMPLETED);
        payment.setStatus(PaymentStatus.SUCCESS);
        log.info("Sipariş #{} başarıyla tamamlandı.",order.getId());
    }

    private void handleFailure(Order order, Payment payment, String failureReason) {
        order.transitionTo(OrderStatus.FAILED);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(failureReason);

        log.warn("Sipariş #{} ödemesi başarısız! Stoklar geri yükleniyor...", order.getId());
        for (OrderItem item : order.getItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }
}
