package com.commerceflow.orderservice.service;

import com.commerceflow.orderservice.dto.request.CreateOrderRequest;
import com.commerceflow.orderservice.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest createOrderRequest);
}
