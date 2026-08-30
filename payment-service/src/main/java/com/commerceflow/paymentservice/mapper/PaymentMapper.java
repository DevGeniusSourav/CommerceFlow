package com.commerceflow.paymentservice.mapper;

import com.commerceflow.paymentservice.dto.response.PaymentResponse;
import com.commerceflow.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}
