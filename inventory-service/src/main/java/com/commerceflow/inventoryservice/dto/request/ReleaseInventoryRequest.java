package com.commerceflow.inventoryservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReleaseInventoryRequest {

    @NotNull
    @Positive
    private Long orderId;
}
