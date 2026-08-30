package com.commerceflow.paymentservice.entity;

import com.commerceflow.paymentservice.enums.PaymentStatus;
import com.commerceflow.paymentservice.exception.InvalidPaymentStateException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Builder
    private Payment(
            Long orderId,
            BigDecimal amount
    ) {
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markSuccessful() {
        if (status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(status, PaymentStatus.SUCCESS);
        }

        status = PaymentStatus.SUCCESS;
        updatedAt = Instant.now();
    }

    public void markFailed() {
        if (status != PaymentStatus.PENDING) {
            throw new InvalidPaymentStateException(status, PaymentStatus.FAILED);
        }

        status = PaymentStatus.FAILED;
        updatedAt = Instant.now();
    }
}