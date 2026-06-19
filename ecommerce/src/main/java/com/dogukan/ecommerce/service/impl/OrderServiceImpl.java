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
import com.dogukan.ecommerce.service.OrderService;
import com.dogukan.ecommerce.service.ProductService;
import com.dogukan.ecommerce.util.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request, String userEmail) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException(ErrorType.EMPTY_CART);
        }

        Map<Long, Integer> mergedItems = request.getItems().stream()
                .filter(item -> item.getQuantity() > 0)
                .collect(Collectors.toMap(
                        OrderItemRequest::getProductId,
                        OrderItemRequest::getQuantity,
                        Integer::sum
                ));

        if (mergedItems.isEmpty()) {
            throw new BusinessException(ErrorType.INVALID_ORDER_ITEMS);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorType.USER_NOT_FOUND));

        Order order = Order.builder()
                .user(user)
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Map<Long, Product> validatedProducts = productService.decreaseStocksAndGet(mergedItems);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : mergedItems.entrySet()) {
            Product product = validatedProducts.get(entry.getKey());
            Integer quantity = entry.getValue();

            BigDecimal itemTotalAmount = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemTotalAmount);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);
        log.info("Sipariş oluşturuldu. OrderId: {}, User: {}, Tutar: {}",
                savedOrder.getId(), userEmail, savedOrder.getTotalAmount());

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = orderRepository.findAllByOrderStatusAndCreatedAtBefore(
                OrderStatus.PENDING,
                expirationTime
        );
        if (expiredOrders.isEmpty()) {
            return;
        }
        log.info("{} adet süresi dolmuş ödenmeyen sipariş iptal ediliyor...", expiredOrders.size());

        Map<Long, Integer> productQuantitiesToRestore = new HashMap<>();
        for (Order order : expiredOrders) {
            order.transitionTo(OrderStatus.FAILED);
            orderRepository.save(order);
            log.info("Sipariş #{} ödenmediği için otomatik iptal edildi.", order.getId());
            for (OrderItem item : order.getItems()) {
                productQuantitiesToRestore.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
            }
        }
        if (!productQuantitiesToRestore.isEmpty()) {
            productService.restoreStockBulk(productQuantitiesToRestore);
            log.info("Toplam {} ürün için stok iadesi yapıldı.", productQuantitiesToRestore.size());
        }
    }

    @Override
    public List<OrderResponse> getOrdersByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorType.USER_NOT_FOUND));

        List<Order> orders = orderRepository.findByUserId(user.getId());

        return orderMapper.toResponseList(orders);
    }

    @Override
    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Order orderById = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorType.ORDER_NOT_FOUND));

        if(!orderById.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException(ErrorType.FORBIDDEN_USER_ACT);
        }

        return orderMapper.toResponse(orderById);
    }
}
