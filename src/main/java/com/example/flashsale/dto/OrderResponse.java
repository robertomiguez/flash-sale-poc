package com.example.flashsale.dto;

import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.model.OrderStatus;

import java.time.Instant;

public record OrderResponse(String id, Long itemId, Long userId, OrderStatus status, Instant createdAt) {
    public static OrderResponse from(OrderEntity order) {
        return new OrderResponse(order.getId(), order.getItemId(), order.getUserId(), order.getStatus(),
                order.getCreatedAt());
    }
}
