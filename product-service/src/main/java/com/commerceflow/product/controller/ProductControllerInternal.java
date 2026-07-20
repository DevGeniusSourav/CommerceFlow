package com.commerceflow.product.controller;

import com.commerceflow.product.dto.response.ProductSummaryResponse;
import com.commerceflow.product.service.ProductService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internal/products")
public class ProductControllerInternal {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductSummaryResponse> getProductSummary(@PathVariable @Positive Long id) {
        return ResponseEntity.ok().body(productService.getProductSummary(id));
    }
}
