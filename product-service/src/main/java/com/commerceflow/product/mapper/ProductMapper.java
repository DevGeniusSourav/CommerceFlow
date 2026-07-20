package com.commerceflow.product.mapper;

import com.commerceflow.product.dto.request.CreateProductRequest;
import com.commerceflow.product.dto.response.ProductResponse;
import com.commerceflow.product.dto.response.ProductSummaryResponse;
import com.commerceflow.product.entity.Product;
import com.commerceflow.product.enums.ProductStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {

        Instant now = Instant.now();

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .sellerId(request.getSellerId())
                .status(ProductStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .sellerId(product.getSellerId())
                .imageUrls(List.of())   // temporary
                .build();
    }

    public ProductSummaryResponse toProductSummary(Product product) {
        return new ProductSummaryResponse(product.getId(), product.getName(), product.getPrice(), product.getStatus());
    }
}