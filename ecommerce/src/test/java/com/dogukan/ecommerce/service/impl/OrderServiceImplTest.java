package com.dogukan.ecommerce.service.impl;

import com.dogukan.ecommerce.dto.request.OrderCreateRequest;
import com.dogukan.ecommerce.dto.request.OrderItemRequest;
import com.dogukan.ecommerce.dto.response.OrderResponse;
import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.OrderItem;
import com.dogukan.ecommerce.entity.Product;
import com.dogukan.ecommerce.entity.User;
import com.dogukan.ecommerce.exception.BusinessException;
import com.dogukan.ecommerce.exception.helper.ErrorType;
import com.dogukan.ecommerce.mapper.OrderMapper;
import com.dogukan.ecommerce.repository.OrderRepository;
import com.dogukan.ecommerce.repository.UserRepository;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductService productService;

    @Mock
    private OrderMapper orderMapper;

    @Test
    void when_createOrder_success_then_returnOrderResponse() {
        String userEmail = "test@test.com";
        Long productId = 1L;

        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(productId)
                                .quantity(2)
                                .build()
                ))
                .build();

        User user = Instancio.create(User.class);
        OrderResponse expectedResponse = Instancio.create(OrderResponse.class);

        Product product = Instancio.of(Product.class)
                .set(field(Product::getPrice), BigDecimal.valueOf(100))
                .set(field(Product::getStock), 10)
                .create();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(productService.decreaseStocksAndGet(any())).thenReturn(Map.of(productId, product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toResponse(any())).thenReturn(expectedResponse);

        OrderResponse response = orderService.createOrder(request, userEmail);

        assertNotNull(response);
        assertEquals(expectedResponse, response);
        verify(orderRepository).save(argThat(order ->
                order.getOrderStatus() == OrderStatus.PENDING &&
                        order.getTotalAmount().compareTo(BigDecimal.valueOf(200)) == 0
        ));
    }

    @Test
    void when_createOrder_emptyCart_then_throwException() {
        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(Collections.emptyList())
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(request, "test@test.com"));

        assertEquals(ErrorType.EMPTY_CART, ex.getErrorType());
        verify(orderRepository, never()).save(any());
        verify(productService, never()).decreaseStocksAndGet(any());
    }

    @Test
    void when_createOrder_allItemsZeroQuantity_then_throwException() {
        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(1L)
                                .quantity(0)
                                .build()
                ))
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(request, "test@test.com"));

        assertEquals(ErrorType.INVALID_ORDER_ITEMS, ex.getErrorType());
        verify(userRepository, never()).findByEmail(any());
        verify(productService, never()).decreaseStocksAndGet(any());
    }

    @Test
    void when_createOrder_userNotFound_then_throwException() {
        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(1L)
                                .quantity(1)
                                .build()
                ))
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(request, "test@test.com"));

        assertEquals(ErrorType.USER_NOT_FOUND, ex.getErrorType());
        verify(productService, never()).decreaseStocksAndGet(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void when_createOrder_notEnoughStock_then_throwException() {
        String userEmail = "test@test.com";

        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder()
                                .productId(1L)
                                .quantity(1)
                                .build()
                ))
                .build();

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(Instancio.create(User.class)));
        when(productService.decreaseStocksAndGet(any()))
                .thenThrow(new BusinessException(ErrorType.NOT_ENOUGH_STOCK));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createOrder(request, userEmail));

        assertEquals(ErrorType.NOT_ENOUGH_STOCK, ex.getErrorType());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void when_createOrder_duplicateProducts_then_quantitiesMerged() {
        String userEmail = "test@test.com";
        Long productId = 1L;

        OrderCreateRequest request = OrderCreateRequest.builder()
                .items(List.of(
                        OrderItemRequest.builder().productId(productId).quantity(2).build(),
                        OrderItemRequest.builder().productId(productId).quantity(3).build()
                ))
                .build();

        Product product = Instancio.of(Product.class)
                .set(field(Product::getPrice), BigDecimal.valueOf(50))
                .create();

        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(Instancio.create(User.class)));
        when(productService.decreaseStocksAndGet(argThat(map ->
                map.containsKey(productId) && map.get(productId).equals(5)
        ))).thenReturn(Map.of(productId, product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toResponse(any())).thenReturn(Instancio.create(OrderResponse.class));

        orderService.createOrder(request, userEmail);

        verify(productService).decreaseStocksAndGet(argThat(map ->
                map.containsKey(productId) && map.get(productId).equals(5)
        ));
        verify(orderRepository).save(argThat(order ->
                order.getTotalAmount().compareTo(BigDecimal.valueOf(250)) == 0
        ));
    }

    @Test
    void when_cancelExpiredOrders_noExpiredOrders_then_doNothing() {
        when(orderRepository.findAllByOrderStatusAndCreatedAtBefore(
                eq(OrderStatus.PENDING),
                any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        orderService.cancelExpiredOrders();

        verify(orderRepository, never()).save(any());
        verify(productService, never()).restoreStockBulk(any());
    }

    @Test
    void when_cancelExpiredOrders_hasExpiredOrders_then_cancelAndRestoreStocks() {
        Product product = Instancio.of(Product.class)
                .set(field(Product::getId),1L)
                .create();

        OrderItem orderItem = Instancio.of(OrderItem.class)
                .set(field(OrderItem::getProduct),product)
                .set(field(OrderItem::getQuantity),3)
                .create();

        Order expiredOrder = Instancio.of(Order.class)
                .set(field(Order::getOrderStatus),OrderStatus.PENDING)
                .set(field(Order::getItems),List.of(orderItem))
                .create();

        when(orderRepository.findAllByOrderStatusAndCreatedAtBefore(
                eq(OrderStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(expiredOrder));

        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderService.cancelExpiredOrders();

        assertEquals(OrderStatus.FAILED, expiredOrder.getOrderStatus());

        verify(orderRepository,times(1)).save(any(Order.class));

        verify(productService).restoreStockBulk(argThat(map ->
                map.containsKey(1L) && map.get(1L).equals(3)));
    }

    @Test
    void when_cancelExpiredOrders_sameProductInMultipleOrders_then_quantitiesMerged() {
        Product product = Instancio.of(Product.class)
                .set(field(Product::getId),1L)
                .create();

        OrderItem orderItem = Instancio.of(OrderItem.class)
                .set(field(OrderItem::getProduct),product)
                .set(field(OrderItem::getQuantity),3)
                .create();

        OrderItem orderItem2 = Instancio.of(OrderItem.class)
                .set(field(OrderItem::getProduct),product)
                .set(field(OrderItem::getQuantity),4)
                .create();

        Order expiredOrder = Instancio.of(Order.class)
                .set(field(Order::getOrderStatus),OrderStatus.PENDING)
                .set(field(Order::getItems),new ArrayList<>(List.of(orderItem)))
                .create();

        Order expiredOrder2 = Instancio.of(Order.class)
                .set(field(Order::getOrderStatus),OrderStatus.PENDING)
                .set(field(Order::getItems),new ArrayList<>(List.of(orderItem2)))
                .create();

        when(orderRepository.findAllByOrderStatusAndCreatedAtBefore(
                eq(OrderStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(expiredOrder, expiredOrder2));

        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        orderService.cancelExpiredOrders();

        assertEquals(OrderStatus.FAILED, expiredOrder.getOrderStatus());
        assertEquals(OrderStatus.FAILED, expiredOrder2.getOrderStatus());

        verify(orderRepository, times(2)).save(any());

        verify(productService).restoreStockBulk(argThat(map ->
                map.containsKey(1L) && map.get(1L).equals(7)));
    }

    @Test
    void when_getOrdersByUserEmail_userNotFound_then_throwException() {
        String userEmail = "test@test.test";

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> orderService.getOrdersByUserEmail(userEmail));

        verify(orderRepository, never()).findById(any());
        verify(orderMapper, never()).toResponseList(anyList());
    }

    @Test
    void when_getOrdersByUserEmail_success_returnsOrderList() {
        String userEmail = "test@test.test";

        User user = Instancio.of(User.class)
                        .set(field(User::getEmail),userEmail)
                                .create();

        List<Order> orderList = Instancio.ofList(Order.class)
                        .create();


        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(orderRepository.findByUserId(user.getId())).thenReturn(orderList);

        List<OrderResponse> result = orderService.getOrdersByUserEmail(userEmail);

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(orderRepository, times(1)).findByUserId(user.getId());
        verify(orderMapper, times(1)).toResponseList(orderList);
        assertEquals(result,orderMapper.toResponseList(orderList));
    }

    @Test
    void when_getOrdersByUserEmail_noOrders_returnEmptyList() {
        String userEmail = "test@test.com";

        User user = Instancio.of(User.class)
                .set(field(User::getEmail),userEmail)
                .create();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(orderRepository.findByUserId(user.getId())).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getOrdersByUserEmail(userEmail);

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(orderRepository, times(1)).findByUserId(user.getId());
        assertEquals(Collections.emptyList(), orderRepository.findByUserId(user.getId()));
        assertEquals(result,Collections.emptyList());

    }

    @Test
    void when_getOrderById_orderNotFound_then_throwException() {
        Long orderId = 1L;
        String userEmail = "test@test.com";

        assertThrows(BusinessException.class, () -> orderService.getOrderById(orderId,userEmail));

        verify(orderMapper, never()).toResponseList(anyList());
    }

    @Test
    void when_getOrderById_forbiddenUser_then_throwException() {
        Long orderId = 1L;
        String requestingUserEmail = "test@test.com";

        User owner = Instancio.of(User.class)
                .set(field(User::getEmail), "owner@test.com")
                .create();

        Order order = Instancio.of(Order.class)
                .set(field(Order::getUser), owner)
                .create();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.getOrderById(orderId, requestingUserEmail));
        assertEquals(ErrorType.FORBIDDEN_USER_ACT, ex.getErrorType());

        verify(orderMapper, never()).toResponse(any());
    }

    @Test
    void when_getOrderById_success_returnOrder() {
        Long orderId = 1L;

        User owner = Instancio.of(User.class)
                .set(field(User::getEmail), "owner@test.com")
                .create();

        Order order = Instancio.of(Order.class)
                .set(field(Order::getUser), owner)
                .create();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.getOrderById(orderId, owner.getEmail());

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderMapper, times(1)).toResponse(order);
    }
}