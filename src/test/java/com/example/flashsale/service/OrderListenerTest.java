package com.example.flashsale.service;

import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.model.OrderStatus;
import com.example.flashsale.repository.OrderRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderListenerTest {
    @Test
    void handleMarksOrderConfirmed() {
        InventoryService inventoryService = mock(InventoryService.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderListener listener = new OrderListener(inventoryService, orderRepository);
        OrderEntity order = new OrderEntity("order-1", 1L, 123L, OrderStatus.PENDING, Instant.now());

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        listener.handle("order-1|1|123|PENDING|false");

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void handleThrowsWhenForceFailIsTrue() {
        InventoryService inventoryService = mock(InventoryService.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderListener listener = new OrderListener(inventoryService, orderRepository);

        assertThrows(IllegalStateException.class, () -> listener.handle("order-1|1|123|PENDING|true"));
    }

    @Test
    void handleDeadLetterRestoresStockAndMarksOrderFailed() {
        InventoryService inventoryService = mock(InventoryService.class);
        OrderRepository orderRepository = mock(OrderRepository.class);
        OrderListener listener = new OrderListener(inventoryService, orderRepository);
        OrderEntity order = new OrderEntity("order-1", 1L, 123L, OrderStatus.PENDING, Instant.now());

        when(orderRepository.findById("order-1")).thenReturn(Optional.of(order));

        listener.handleDeadLetter("order-1|1|123|PENDING|true");

        verify(inventoryService).restoreStock(1L);
        assertEquals(OrderStatus.FAILED, order.getStatus());
    }
}
