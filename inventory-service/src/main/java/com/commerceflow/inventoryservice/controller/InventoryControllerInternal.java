package com.commerceflow.inventoryservice.controller;

import com.commerceflow.inventoryservice.dto.request.ConfirmInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.ReleaseInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.ReserveInventoryRequest;
import com.commerceflow.inventoryservice.dto.response.ReservationResponse;
import com.commerceflow.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryControllerInternal {

    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserve(@Valid @RequestBody ReserveInventoryRequest reserveInventoryRequest) {
        return ResponseEntity.ok(inventoryService.reserve(reserveInventoryRequest));
    }

    @PostMapping("/release")
    public ResponseEntity<ReservationResponse> release(@Valid @RequestBody ReleaseInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.release(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ReservationResponse> confirm(@Valid @RequestBody ConfirmInventoryRequest confirmInventoryRequest) {
        return ResponseEntity.ok(inventoryService.confirm(confirmInventoryRequest));
    }
}
