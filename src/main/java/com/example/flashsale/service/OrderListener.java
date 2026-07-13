package com.example.flashsale.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.model.OrderStatus;
import com.example.flashsale.repository.OrderRepository;

@Component
public class OrderListener {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);

    private final InventoryService inventoryService;
    private final OrderRepository orderRepository;

    public OrderListener(InventoryService inventoryService, OrderRepository orderRepository) {
        this.inventoryService = inventoryService;
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    @Transactional
    public void handle(String message) {
        OrderService.OrderEvent event = OrderService.OrderEvent.fromMessage(message);
        OrderEntity order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + event.orderId()));
        order.setStatus(OrderStatus.CONFIRMED);
        log.info("Confirmed order {}", event.orderId());
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}.dlq")
    @Transactional
    public void handleDeadLetter(String message) {
        OrderService.OrderEvent event = OrderService.OrderEvent.fromMessage(message);
        inventoryService.restoreStock(event.itemId());
        orderRepository.findById(event.orderId()).ifPresent(order -> order.setStatus(OrderStatus.FAILED));
        log.warn("Restored stock for item {} after dead-lettered order {}", event.itemId(), event.orderId());
    }
}
