package com.dogukan.ecommerce.mapper;

import com.dogukan.ecommerce.dto.response.OrderItemResponse;
import com.dogukan.ecommerce.dto.response.OrderResponse;
import com.dogukan.ecommerce.entity.Order;
import com.dogukan.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toItemResponse(OrderItem orderItem);

    List<OrderResponse> toResponseList(List<Order> orders);
}
