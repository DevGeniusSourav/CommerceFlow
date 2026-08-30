package com.commerceflow.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveInventoryItemRequest(

        @NotNull
        @Positive
        Long productId,

        @NotNull
        @Positive
        Integer quantity
) {
}