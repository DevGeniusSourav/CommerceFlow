package com.commerceflow.orderservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReleaseInventoryRequest(

        @NotNull
        @Positive
        Long orderId

) {
}