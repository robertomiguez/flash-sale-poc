package com.example.flashsale.dto;

import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderResponseTest {
    @Test
    void fromMapsEntityFields() {
        OrderEntity order = new OrderEntity("order-1", 1L, 123L, OrderStatus.CONFIRMED,
                Instant.parse("2026-07-13T18:00:00Z"));

        OrderResponse response = OrderResponse.from(order);

        assertEquals("order-1", response.id());
        assertEquals(1L, response.itemId());
        assertEquals(123L, response.userId());
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(Instant.parse("2026-07-13T18:00:00Z"), response.createdAt());
    }
}
