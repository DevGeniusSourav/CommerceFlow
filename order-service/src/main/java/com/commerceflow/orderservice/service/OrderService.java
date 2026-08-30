package com.commerceflow.orderservice.service;

import com.commerceflow.orderservice.dto.request.CreateOrderRequest;
import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.dto.response.PaymentResponse;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest createOrderRequest);

    PaymentResponse processPayment(Long orderId);

}
