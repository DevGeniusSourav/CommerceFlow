package com.commerceflow.inventoryservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {

    private Long id;

    private Long productId;

    private Integer totalQuantity;

    private Integer reservedQuantity;
}
