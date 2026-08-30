package com.commerceflow.orderservice.controller;

import com.commerceflow.orderservice.dto.request.CreateOrderRequest;
import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.dto.response.PaymentResponse;
import com.commerceflow.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(createOrderRequest));
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                orderService.processPayment(orderId)
        );
    }
}
