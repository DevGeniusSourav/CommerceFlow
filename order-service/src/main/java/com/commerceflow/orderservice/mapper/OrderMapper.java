package com.commerceflow.orderservice.mapper;

import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {

        return OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus()).build();
    }
}
