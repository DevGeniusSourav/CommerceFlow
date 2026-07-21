package com.commerceflow.orderservice.service.impl;

import com.commerceflow.orderservice.client.ProductClient;
import com.commerceflow.orderservice.dto.request.CreateOrderRequest;
import com.commerceflow.orderservice.dto.request.OrderItemRequest;
import com.commerceflow.orderservice.dto.response.OrderResponse;
import com.commerceflow.orderservice.dto.response.ProductSummaryResponse;
import com.commerceflow.orderservice.entity.Order;
import com.commerceflow.orderservice.entity.OrderItem;
import com.commerceflow.orderservice.enums.ProductStatus;
import com.commerceflow.orderservice.exception.ProductUnavailableException;
import com.commerceflow.orderservice.mapper.OrderMapper;
import com.commerceflow.orderservice.repository.OrderRepository;
import com.commerceflow.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ProductClient productClient;

    @Override
    public OrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        Order order = Order.create(createOrderRequest.getCustomerId());

        for(OrderItemRequest itemRequest: createOrderRequest.getItems()){
            ProductSummaryResponse product = productClient.getProduct(itemRequest.getProductId());
            if(product.status()!= ProductStatus.ACTIVE){
                throw new ProductUnavailableException(product.id());
            }

            OrderItem orderItem = OrderItem.create(
                    product.id(),
                    product.name(),
                    product.price(),
                    itemRequest.getQuantity());

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
}
