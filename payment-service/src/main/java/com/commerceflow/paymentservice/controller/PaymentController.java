package com.commerceflow.paymentservice.controller;

import com.commerceflow.paymentservice.dto.request.CreatePaymentRequest;
import com.commerceflow.paymentservice.dto.response.PaymentResponse;
import com.commerceflow.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return ResponseEntity.ok(
                paymentService.createPayment(request)
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                paymentService.getPayment(orderId)
        );
    }

    @PostMapping("/{orderId}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                paymentService.processPayment(orderId)
        );
    }
}