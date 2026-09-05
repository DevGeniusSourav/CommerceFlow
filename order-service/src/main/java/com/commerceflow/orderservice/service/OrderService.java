package com.commerceflow.orderservice.service;

import com.commerceflow.orderservice.dto.request.CreateOrderRequest;
import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.dto.response.PaymentResponse;
import com.commerceflow.orderservice.kafka.event.InventoryConfirmedEvent;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest createOrderRequest);

    PaymentResponse processPayment(Long orderId);

    void handleInventoryConfirmed(InventoryConfirmedEvent event);

}
