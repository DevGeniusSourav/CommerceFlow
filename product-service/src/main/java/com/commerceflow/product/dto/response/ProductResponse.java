package com.commerceflow.product.dto.response;

import com.commerceflow.product.enums.Category;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Category category;

    private Long sellerId;

    private List<String> imageUrls;

}