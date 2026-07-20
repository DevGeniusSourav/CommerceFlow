package com.commerceflow.orderservice.dto.response;

import com.commerceflow.orderservice.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;

    private OrderStatus status;

    private BigDecimal totalAmount;
}
