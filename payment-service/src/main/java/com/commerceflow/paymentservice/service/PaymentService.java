package com.commerceflow.paymentservice.service;

import com.commerceflow.paymentservice.dto.request.CreatePaymentRequest;
import com.commerceflow.paymentservice.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse getPayment(Long orderId);

    PaymentResponse processPayment(Long orderId);
}