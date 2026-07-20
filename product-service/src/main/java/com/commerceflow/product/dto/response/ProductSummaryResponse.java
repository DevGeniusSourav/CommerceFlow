package com.commerceflow.product.dto.response;

import com.commerceflow.product.enums.ProductStatus;

import java.math.BigDecimal;

public record ProductSummaryResponse(

        Long id,

        String name,

        BigDecimal price,

        ProductStatus status

) {}
