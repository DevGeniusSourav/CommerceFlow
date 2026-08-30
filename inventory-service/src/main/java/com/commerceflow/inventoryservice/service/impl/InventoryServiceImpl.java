package com.commerceflow.inventoryservice.service.impl;

import com.commerceflow.inventoryservice.dto.request.*;
import com.commerceflow.inventoryservice.dto.response.InventoryResponse;
import com.commerceflow.inventoryservice.dto.response.ReservationResponse;
import com.commerceflow.inventoryservice.entity.Inventory;
import com.commerceflow.inventoryservice.entity.InventoryReservation;
import com.commerceflow.inventoryservice.enums.ReservationStatus;
import com.commerceflow.inventoryservice.exception.InsufficientInventoryException;
import com.commerceflow.inventoryservice.exception.InventoryNotFoundException;
import com.commerceflow.inventoryservice.exception.InventoryReservationAlreadyExists;
import com.commerceflow.inventoryservice.exception.InventoryReservationNotFound;
import com.commerceflow.inventoryservice.mapper.InventoryMapper;
import com.commerceflow.inventoryservice.repository.InventoryRepository;
import com.commerceflow.inventoryservice.repository.InventoryReservationRepository;
import com.commerceflow.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository inventoryReservationRepository;

    @Override
    public InventoryResponse create(CreateInventoryRequest request) {
        Inventory inventory = inventoryMapper.toEntity(request);
        return inventoryMapper.toResponse(
                inventoryRepository.save(inventory)
        );
    }

    @Override
    public List<InventoryResponse> getInventories(Long productId) {
        List<Inventory> inventories =
                inventoryRepository.findByProductId(productId);

        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException(productId);
        }

        return inventories.stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservationResponse reserve(ReserveInventoryRequest request) {
        List<ReservationItemRequest> items =
                request.items()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        ReservationItemRequest::productId
                                )
                        )
                        .toList();

        for (ReservationItemRequest item : items) {
            reserveItem(
                    request.orderId(),
                    item.productId(),
                    item.quantity()
            );
        }

        return ReservationResponse.builder()
                .success(true)
                .message("All inventory reserved")
                .build();
    }

    private void reserveItem(
            Long orderId,
            Long productId,
            Integer quantity
    ) {

        InventoryReservation existingReservation =
                inventoryReservationRepository
                        .findByOrderIdAndProductIdAndStatus(
                                orderId,
                                productId,
                                ReservationStatus.RESERVED
                        )
                        .orElse(null);

        if (existingReservation != null) {
            validateExistingReservation(
                    existingReservation,
                    quantity,
                    orderId,
                    productId
            );
            return;
        }

        List<Inventory> inventories =
                inventoryRepository.findByProductIdForUpdate(productId);

        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException(productId);
        }

        existingReservation =
                inventoryReservationRepository
                        .findByOrderIdAndProductIdAndStatus(
                                orderId,
                                productId,
                                ReservationStatus.RESERVED
                        )
                        .orElse(null);

        if (existingReservation != null) {
            validateExistingReservation(
                    existingReservation,
                    quantity,
                    orderId,
                    productId
            );
            return;
        }

        Inventory reservedInventory = null;

        for (Inventory inventory : inventories) {
            if (inventory.reserve(quantity)) {
                reservedInventory = inventory;
                break;
            }
        }

        if (reservedInventory == null) {
            throw new InsufficientInventoryException(
                    productId,
                    quantity
            );
        }

        inventoryRepository.save(reservedInventory);

        InventoryReservation reservation =
                InventoryReservation.builder()
                        .orderId(orderId)
                        .productId(productId)
                        .inventoryId(reservedInventory.getId())
                        .quantity(quantity)
                        .status(ReservationStatus.RESERVED)
                        .createdAt(Instant.now())
                        .build();

        inventoryReservationRepository.save(reservation);
    }

    private void validateExistingReservation(
            InventoryReservation existingReservation,
            Integer requestedQuantity,
            Long orderId,
            Long productId
    ) {

        if (!existingReservation.getQuantity().equals(requestedQuantity)) {
            throw new InventoryReservationAlreadyExists(
                    orderId,
                    productId
            );
        }
    }

    @Override
    @Transactional
    public ReservationResponse release(
            ReleaseInventoryRequest request
    ) {

        List<InventoryReservation> reservations =
                inventoryReservationRepository
                        .findAllByOrderIdAndStatus(
                                request.getOrderId(),
                                ReservationStatus.RESERVED
                        );

        if (reservations.isEmpty()) {
            return ReservationResponse.builder()
                    .success(true)
                    .message("Inventory already released")
                    .build();
        }

        for (InventoryReservation reservation : reservations) {

            Inventory inventory =
                    inventoryRepository
                            .findById(reservation.getInventoryId())
                            .orElseThrow(() ->
                                    new InventoryNotFoundException(
                                            reservation.getInventoryId()
                                    )
                            );

            inventory.release(reservation.getQuantity());
            reservation.release();
        }

        return ReservationResponse.builder()
                .success(true)
                .message("Inventory released")
                .build();
    }

    @Override
    @Transactional
    public ReservationResponse confirm(ConfirmInventoryRequest request) {

        List<InventoryReservation> reservations =
                inventoryReservationRepository
                        .findAllByOrderIdAndStatus(
                                request.getOrderId(),
                                ReservationStatus.RESERVED
                        );

        if (reservations.isEmpty()) {
            throw new InventoryReservationNotFound(request.getOrderId());
        }

        for (InventoryReservation reservation : reservations) {

            Inventory inventory =
                    inventoryRepository
                            .findById(reservation.getInventoryId())
                            .orElseThrow(() ->
                                    new InventoryNotFoundException(
                                            reservation.getInventoryId()
                                    )
                            );

            inventory.confirm(reservation.getQuantity());
            reservation.confirm();
        }

        return ReservationResponse.builder()
                .success(true)
                .message("All inventory confirmed")
                .build();
    }

    @Override
    @Transactional
    public InventoryResponse restock(
            RestockInventoryRequest request
    ) {

        List<Inventory> inventories =
                inventoryRepository.findByProductId(
                        request.getProductId()
                );

        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException(
                    request.getProductId()
            );
        }

        Inventory inventory = inventories.getFirst();

        inventory.restock(request.getQuantity());

        return inventoryMapper.toResponse(inventory);
    }

}
