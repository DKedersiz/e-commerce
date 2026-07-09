package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.event.OrderCompletedEvent;
import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.OrderItem;
import com.dogukan.ecommerce.entity.ProcessedEvent;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.repository.OrderRepository;
import com.dogukan.ecommerce.repository.ProcessedEventRepository;
import com.dogukan.ecommerce.service.PaymentService;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${spring.stripe.api.key}")
    private String stripeApiKey;

    @Value("${spring.stripe.webhook.cancel-url}")
    private String cancelUrl;

    @Value("${spring.stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${spring.stripe.webhook.success-url}")
    private String successUrl;

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }


    @Override
    @Transactional
    public String createCheckoutSession(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorType.ORDER_NOT_FOUND));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BusinessException(ErrorType.FORBIDDEN_USER_ACT);
        }

        try {
            long amountInCents = order.getTotalAmount()
                    .multiply(new BigDecimal(100))
                    .longValue();

            long expiresAt = Instant.now().plus(30, ChronoUnit.MINUTES).getEpochSecond();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setCustomerEmail(order.getUser().getEmail())
                    .putMetadata("orderId", orderId.toString())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("try")
                                                    .setUnitAmount(amountInCents)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("DogukanShop Siparişi #" + order.getId())
                                                                    .setDescription("Siparişinizin toplam ödemesi.")
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey("checkout_session_" + orderId)
                    .build();

            Session session = Session.create(params);
            log.info("Sipariş #{} için Stripe Checkout Session oluşturuldu. Session ID: {}", orderId, session.getId());
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Stripe API hatası: {}", e.getMessage());
            throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "products_cache", allEntries = true)
    public void handleStripeWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe Webhook imza doğrulama hatası: {}", e.getMessage());
            throw new BusinessException(ErrorType.FORBIDDEN_USER_ACT);
        }

        if (processedEventRepository.existsByStripeEventId(event.getId())) {
            log.info("Stripe Event {} zaten işlenmiş, atlanıyor (Deduplication).", event.getId());
            return;
        }
        log.info("Stripe Webhook Event işleniyor. Tip: {}, ID: {}", event.getType(), event.getId());
        try {
            switch (event.getType()) {
                case "checkout.session.completed" -> {
                    Session session = (Session) event.getDataObjectDeserializer().getObject()
                            .orElseThrow(() -> new BusinessException(ErrorType.INTERNAL_SERVER_ERROR));
                    handleCheckoutCompleted(session);
                }
                case "checkout.session.expired" -> {
                    Session session = (Session) event.getDataObjectDeserializer().getObject()
                            .orElseThrow(() -> new BusinessException(ErrorType.INTERNAL_SERVER_ERROR));
                    handleCheckoutExpired(session);
                }
                default -> log.info("İşlenmeyen event tipi yoksayıldı: {}", event.getType());
            }

            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .stripeEventId(event.getId())
                            .processedAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            log.error("Webhook event işlenirken hata oluştu: {}", e.getMessage());
            throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleCheckoutCompleted(Session session) {
        String orderIdStr = session.getMetadata().get("orderId");
        Long orderId = Long.parseLong(orderIdStr);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorType.ORDER_NOT_FOUND));

        if (order.getOrderStatus() == OrderStatus.COMPLETED) {
            return;
        }

        order.transitionTo(OrderStatus.COMPLETED);
        orderRepository.save(order);
        log.info("Sipariş #{} başarıyla ödendi ve COMPLETED yapıldı.", orderId);

        OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(
                order.getId(),
                order.getUser().getEmail(),
                order.getTotalAmount()
        );
        eventPublisher.publishEvent(orderCompletedEvent);
    }

    private void handleCheckoutExpired(Session session) {
        String orderIdStr = session.getMetadata().get("orderId");
        Long orderId = Long.parseLong(orderIdStr);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorType.ORDER_NOT_FOUND));

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            order.transitionTo(OrderStatus.FAILED);
            orderRepository.save(order);
            Map<Long, Integer> productQuantitiesToRestore = new HashMap<>();
            for (OrderItem item : order.getItems()) {
                productQuantitiesToRestore.merge(
                        item.getProduct().getId(),
                        item.getQuantity(),
                        Integer::sum
                );
            }

            if (!productQuantitiesToRestore.isEmpty()) {
                productService.restoreStockBulk(productQuantitiesToRestore);
                log.info("Sipariş #{} süresi dolduğu için iptal edildi. {} ürünün stoku iade edildi.", orderId, productQuantitiesToRestore.size());
            }
        }
    }
}
