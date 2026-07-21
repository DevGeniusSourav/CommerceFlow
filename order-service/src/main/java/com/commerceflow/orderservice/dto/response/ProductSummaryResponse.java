package com.commerceflow.orderservice.dto.response;

import com.commerceflow.orderservice.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductSummaryResponse(

        Long id,

        String name,

        BigDecimal price,

        ProductStatus status

) {}
