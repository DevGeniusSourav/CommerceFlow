package com.commerceflow.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Long productId;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    void assignOrder(Order order) {
        this.order = order;
    }

    public static OrderItem create(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity
    ){
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new OrderItem(productId, productName, unitPrice, quantity, subtotal);
    }

    @Builder(access = AccessLevel.PRIVATE)
    private OrderItem(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal subtotal
    ){
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
}
