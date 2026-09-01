package com.commerceflow.orderservice.repository;

import com.commerceflow.orderservice.entity.OutboxEvent;
import com.commerceflow.orderservice.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}