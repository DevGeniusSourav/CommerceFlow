package com.commerceflow.inventoryservice.service;

import com.commerceflow.inventoryservice.dto.request.ConfirmInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.CreateInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.ReleaseInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.ReserveInventoryRequest;
import com.commerceflow.inventoryservice.dto.request.RestockInventoryRequest;
import com.commerceflow.inventoryservice.dto.response.InventoryResponse;
import com.commerceflow.inventoryservice.dto.response.ReservationResponse;
import com.commerceflow.inventoryservice.kafka.event.PaymentSucceededEvent;

import java.util.List;

public interface InventoryService {

    InventoryResponse create(CreateInventoryRequest createInventoryRequest);

    List<InventoryResponse> getInventories(Long productId);

    ReservationResponse reserve(ReserveInventoryRequest request);

    ReservationResponse release(ReleaseInventoryRequest request);

    ReservationResponse confirm(ConfirmInventoryRequest request);

    InventoryResponse restock(RestockInventoryRequest request);

    void handlePaymentSucceeded(PaymentSucceededEvent event);
}
