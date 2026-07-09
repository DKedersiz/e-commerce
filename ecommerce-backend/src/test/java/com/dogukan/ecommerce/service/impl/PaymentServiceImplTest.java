package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.User;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.repository.OrderRepository;
import com.dogukan.ecommerce.repository.ProcessedEventRepository;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

    private MockedStatic<Session> mockedSession;
    private MockedStatic<Webhook> mockedWebhook;

    @BeforeEach
    void setUp() {
        mockedSession = mockStatic(Session.class);
        mockedWebhook = mockStatic(Webhook.class);

        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "test-key");
        ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_test");
        ReflectionTestUtils.setField(paymentService, "successUrl", "http://success.url");
        ReflectionTestUtils.setField(paymentService, "cancelUrl", "http://cancel.url");
    }

    @AfterEach
    void tearDown() {
        mockedSession.close();
        mockedWebhook.close();
    }

    @Test
    void when_createCheckoutSession_success_thenReturnSessionUrl() throws Exception {
        Long orderId = 1L;
        User user = Instancio.of(User.class).set(field(User::getEmail), "test@example.com").create();
        Order order = Instancio.of(Order.class)
                .set(field(Order::getId), orderId)
                .set(field(Order::getTotalAmount), new BigDecimal("150.00"))
                .set(field(Order::getUser), user)
                .set(field(Order::getOrderStatus), OrderStatus.PENDING)
                .create();
        Session sessionMock = mock(Session.class);
        when(sessionMock.getUrl()).thenReturn("https://checkout.stripe.com/pay/test");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        mockedSession.when(() -> Session.create(any(com.stripe.param.checkout.SessionCreateParams.class)))
                .thenReturn(sessionMock);

        String url = paymentService.createCheckoutSession(orderId);

        assertNotNull(url);
        assertEquals("https://checkout.stripe.com/pay/test", url);
    }

    @Test
    void when_handleStripeWebhook_invalidSignature_thenThrowBusinessException() throws Exception {
        String payload = "{}";
        String sigHeader = "invalid-sig";

        mockedWebhook.when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), eq("whsec_test")))
                .thenThrow(new SignatureVerificationException("Invalid signature", "sig"));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                paymentService.handleStripeWebhook(payload, sigHeader));
        assertEquals(ErrorType.FORBIDDEN_USER_ACT, exception.getErrorType());
    }
    @Test
    void when_handleStripeWebhook_success_thenUpdateOrderStatusToCompleted() throws Exception {
        String payload = "{}";
        String sigHeader = "valid-sig";
        Long orderId = 123L;

        Event eventMock = mock(Event.class);
        Session sessionMock = mock(Session.class);

        EventDataObjectDeserializer deserializerMock = mock(EventDataObjectDeserializer.class);
        when(eventMock.getType()).thenReturn("checkout.session.completed");
        when(eventMock.getDataObjectDeserializer()).thenReturn(deserializerMock);
        when(deserializerMock.getObject()).thenReturn(Optional.of(sessionMock));
        when(sessionMock.getMetadata()).thenReturn(Map.of("orderId", orderId.toString()));
        when(processedEventRepository.existsByStripeEventId(any())).thenReturn(false);

        Order order = Instancio.of(Order.class)
                .set(field(Order::getId), orderId)
                .set(field(Order::getOrderStatus), OrderStatus.PENDING)
                .create();

        mockedWebhook.when(() -> Webhook.constructEvent(eq(payload), eq(sigHeader), eq("whsec_test")))
                .thenReturn(eventMock);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        paymentService.handleStripeWebhook(payload, sigHeader);
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        verify(orderRepository, times(1)).save(order);
    }
}