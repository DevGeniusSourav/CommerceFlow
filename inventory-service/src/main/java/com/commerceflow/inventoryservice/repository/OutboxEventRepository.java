package com.commerceflow.inventoryservice.repository;

import com.commerceflow.inventoryservice.entity.OutboxEvent;
import com.commerceflow.inventoryservice.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(
            OutboxEventStatus status
    );
}