package com.commerceflow.inventoryservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ReserveInventoryRequest(

        @NotNull
        @Positive
        Long orderId,

        @NotEmpty
        List<@Valid ReservationItemRequest> items
) {
}