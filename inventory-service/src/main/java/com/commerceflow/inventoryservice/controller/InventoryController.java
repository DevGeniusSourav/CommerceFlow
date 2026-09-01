package com.commerceflow.inventoryservice.controller;

import com.commerceflow.inventoryservice.dto.request.ConfirmInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.CreateInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.RestockInventoryRequest;
import com.commerceflow.inventoryservice.dto.response.InventoryResponse;
import com.commerceflow.inventoryservice.dto.response.ReservationResponse;
import com.commerceflow.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> getInventories(@Positive @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventories(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.create(request));
    }

    @PostMapping("/restock")
    public ResponseEntity<InventoryResponse> restock(@Valid @RequestBody RestockInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.restock(request));
    }
}
