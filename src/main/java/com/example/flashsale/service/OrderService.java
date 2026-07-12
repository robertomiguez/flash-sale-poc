package com.example.flashsale.service;

import com.example.flashsale.dto.ReserveRequest;
import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public OrderService(StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate,
            OrderRepository orderRepository) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.orderRepository = orderRepository;
    }

    public boolean reserve(ReserveRequest request) {
        String stockKey = "item:stock:" + request.itemId();
        Long remaining = redisTemplate.opsForValue().decrement(stockKey);

        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return false;
        }

        String orderId = UUID.randomUUID().toString();
        String eventMessage = OrderEvent.toMessage(orderId, request.itemId(), request.userId(), "PENDING");
        rabbitTemplate.convertAndSend(exchange, routingKey, eventMessage);
        return true;
    }

    public void persistOrder(OrderEvent event) {
        if (orderRepository.existsById(event.orderId())) {
            return;
        }

        OrderEntity entity = new OrderEntity(event.orderId(), event.itemId(), event.userId(), event.status(),
                Instant.now());
        orderRepository.save(entity);
    }

    public record OrderEvent(String orderId, Long itemId, Long userId, String status) {
        public static String toMessage(String orderId, Long itemId, Long userId, String status) {
            return String.join("|", orderId, String.valueOf(itemId), String.valueOf(userId), status);
        }

        public static OrderEvent fromMessage(String message) {
            String[] parts = message.split("\\|", 4);
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid order event message: " + message);
            }

            return new OrderEvent(parts[0], Long.valueOf(parts[1]), Long.valueOf(parts[2]), parts[3]);
        }
    }
}
