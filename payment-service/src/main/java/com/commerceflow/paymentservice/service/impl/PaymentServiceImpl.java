package com.commerceflow.paymentservice.service.impl;

import com.commerceflow.paymentservice.dto.request.CreatePaymentRequest;
import com.commerceflow.paymentservice.dto.response.PaymentResponse;
import com.commerceflow.paymentservice.entity.Payment;
import com.commerceflow.paymentservice.exception.PaymentAlreadyExistsException;
import com.commerceflow.paymentservice.exception.PaymentNotFoundException;
import com.commerceflow.paymentservice.mapper.PaymentMapper;
import com.commerceflow.paymentservice.repository.PaymentRepository;
import com.commerceflow.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new PaymentAlreadyExistsException(request.orderId());
        }

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .amount(request.amount())
                .build();

        return paymentMapper.toResponse(
                paymentRepository.save(payment)
        );
    }

    @Override
    public PaymentResponse getPayment(Long orderId) {

        Payment payment =
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(orderId)
                        );

        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(Long orderId) {

        Payment payment =
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() ->
                                new PaymentNotFoundException(orderId)
                        );

        // Simulated payment gateway authorization.
        // Replace with a real gateway call when available.
        boolean approved = authorize(payment);

        if (approved) {
            payment.markSuccessful();
        } else {
            payment.markFailed();
        }

        return paymentMapper.toResponse(payment);
    }

    /**
     * Simulates a payment gateway authorization.
     * <p>
     * Temporary stand-in until a real gateway is integrated. Kept as a seam so the
     * decision logic can be swapped without touching the status-transition flow.
     */
    private boolean authorize(Payment payment) {
        // Simulation: approve every payment for now.
        // A real implementation would call the gateway and return its decision.
        return true;
    }
}