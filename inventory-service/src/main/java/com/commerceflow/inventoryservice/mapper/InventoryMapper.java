package com.commerceflow.inventoryservice.mapper;

import com.commerceflow.inventoryservice.dto.request.CreateInventoryRequest;
import com.commerceflow.inventoryservice.dto.response.InventoryResponse;
import com.commerceflow.inventoryservice.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "totalQuantity", source = "quantity")
    @Mapping(target = "soldQuantity", constant = "0")
    Inventory toEntity(CreateInventoryRequest createInventoryRequest);

//    Inventory toEntity(ReserveInventoryRequest reserveInventoryRequest);

    InventoryResponse toResponse(Inventory inventory);

//    ReservationResponse toResponseFromReservation(Inventory inventory);
}
