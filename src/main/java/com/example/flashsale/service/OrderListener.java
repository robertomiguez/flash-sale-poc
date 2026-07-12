package com.example.flashsale.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    private static final Logger log = LoggerFactory.getLogger(OrderListener.class);
    private static final int MAX_ATTEMPTS = 3;

    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.dead-letter-exchange}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.dead-letter-routing-key}")
    private String deadLetterRoutingKey;

    public OrderListener(OrderService orderService, RabbitTemplate rabbitTemplate) {
        this.orderService = orderService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handle(String message) {
        Exception lastFailure = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                orderService.persistOrder(OrderService.OrderEvent.fromMessage(message));
                return;
            } catch (Exception ex) {
                lastFailure = ex;
                log.warn("Order listener attempt {} failed: {}", attempt, ex.getMessage());
            }
        }

        log.error("Sending message to dead-letter queue after {} failed attempts", MAX_ATTEMPTS, lastFailure);
        rabbitTemplate.convertAndSend(deadLetterExchange, deadLetterRoutingKey, message);
    }
}
