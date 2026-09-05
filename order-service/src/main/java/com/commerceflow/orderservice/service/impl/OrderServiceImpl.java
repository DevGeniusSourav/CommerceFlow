package com.commerceflow.orderservice.service.impl;

import com.commerceflow.orderservice.client.InventoryClient;
import com.commerceflow.orderservice.client.PaymentClient;
import com.commerceflow.orderservice.client.ProductClient;
import com.commerceflow.orderservice.dto.request.*;
import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.dto.response.PaymentResponse;
import com.commerceflow.orderservice.dto.response.ProductSummaryResponse;
import com.commerceflow.orderservice.entity.Order;
import com.commerceflow.orderservice.entity.OrderItem;
import com.commerceflow.orderservice.entity.ProcessedEvent;
import com.commerceflow.orderservice.enums.OrderStatus;
import com.commerceflow.orderservice.enums.PaymentStatus;
import com.commerceflow.orderservice.enums.ProductStatus;
import com.commerceflow.orderservice.exception.InvalidOrderStateException;
import com.commerceflow.orderservice.exception.OrderNotFoundException;
import com.commerceflow.orderservice.exception.PaymentFailedException;
import com.commerceflow.orderservice.exception.ProductUnavailableException;
import com.commerceflow.orderservice.kafka.event.InventoryConfirmedEvent;
import com.commerceflow.orderservice.mapper.OrderMapper;
import com.commerceflow.orderservice.repository.OrderRepository;
import com.commerceflow.orderservice.repository.ProcessedEventRepository;
import com.commerceflow.orderservice.service.OrderService;
import com.commerceflow.orderservice.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ProductClient productClient;

    private final InventoryClient inventoryClient;

    private final PaymentClient paymentClient;

    private final ProcessedEventRepository processedEventRepository;

    private final OutboxService outboxService;

    @Override
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        Order order = Order.create(createOrderRequest.getCustomerId());

        for (OrderItemRequest itemRequest : createOrderRequest.getItems()) {
            ProductSummaryResponse product =
                    productClient.getProduct(itemRequest.getProductId());

            if (product.status() != ProductStatus.ACTIVE) {
                throw new ProductUnavailableException(product.id());
            }

            OrderItem orderItem = OrderItem.create(
                    product.id(),
                    product.name(),
                    product.price(),
                    itemRequest.getQuantity()
            );

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        ReserveInventoryRequest inventoryRequest =
                new ReserveInventoryRequest(
                        savedOrder.getId(),
                        createOrderRequest.getItems()
                                .stream()
                                .map(item -> new ReserveInventoryItemRequest(
                                        item.getProductId(),
                                        item.getQuantity()
                                ))
                                .toList()
                );

        boolean inventoryReserved = false;

        try {
            inventoryClient.reserve(inventoryRequest);
            inventoryReserved = true;

            paymentClient.createPayment(
                    new CreatePaymentRequest(
                            savedOrder.getId(),
                            savedOrder.getTotalAmount()
                    )
            );

            savedOrder.moveToPendingPayment();
            orderRepository.save(savedOrder);
            return orderMapper.toResponse(savedOrder);

        } catch (RuntimeException ex) {

            if (inventoryReserved) {

                savedOrder.markCompensationPending();
                orderRepository.save(savedOrder);

                try {
                    inventoryClient.release(savedOrder.getId());

                    savedOrder.cancel();
                    orderRepository.save(savedOrder);

                } catch (RuntimeException releaseException) {
                    // Leave order as COMPENSATION_PENDING.
                    // It must be retried later.
                }

            } else {

                savedOrder.cancel();
                orderRepository.save(savedOrder);
            }

            throw ex;
        }

    }

    @Override
    @Transactional(noRollbackFor = PaymentFailedException.class)
    public PaymentResponse processPayment(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStateException(order.getStatus(), OrderStatus.PAID);
        }

        PaymentResponse payment =
                paymentClient.processPayment(orderId);

        if (payment.status() != PaymentStatus.SUCCESS) {
            // Payment declined: release the reserved stock and cancel the order
            // so inventory is not held indefinitely.
            inventoryClient.release(orderId);
            order.cancel();
            orderRepository.save(order);

            throw new PaymentFailedException(
                    "Payment failed for order id: " + orderId + ", status: " + payment.status());
        }

        markInventoryConfirmationPending(order);

        return payment;
    }

    @Override
    @Transactional
    public void handleInventoryConfirmed(
            InventoryConfirmedEvent event
    ) {

        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() ->
                        new OrderNotFoundException(event.orderId())
                );

        order.markPaid();

        processedEventRepository.save(
                new ProcessedEvent(event.eventId())
        );

        outboxService.saveOrderPaidEvent(order);
    }

    private void markInventoryConfirmationPending(Order order) {
        order.moveToInventoryConfirmationPending();
        outboxService.savePaymentSucceededEvent(order);
    }
}
