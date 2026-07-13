package com.example.flashsale.service;

import com.example.flashsale.dto.ReserveRequest;
import com.example.flashsale.model.OrderEntity;
import com.example.flashsale.model.OrderStatus;
import com.example.flashsale.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final StringRedisTemplate redisTemplate;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    public OrderService(StringRedisTemplate redisTemplate,
            OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.redisTemplate = redisTemplate;
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public boolean reserve(ReserveRequest request) {
        String stockKey = "item:stock:" + request.itemId();
        Long remaining = redisTemplate.opsForValue().decrement(stockKey);

        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return false;
        }

        String orderId = UUID.randomUUID().toString();
        OrderEvent event = new OrderEvent(orderId, request.itemId(), request.userId(), OrderStatus.PENDING);
        orderRepository.save(new OrderEntity(orderId, request.itemId(), request.userId(), OrderStatus.PENDING,
                Instant.now()));
        rabbitTemplate.convertAndSend(exchange, routingKey, event.toMessage());
        return true;
    }

    public record OrderEvent(String orderId, Long itemId, Long userId, OrderStatus status) {
        public String toMessage() {
            return String.join("|", orderId, String.valueOf(itemId), String.valueOf(userId), status.name());
        }

        public static OrderEvent fromMessage(String message) {
            String[] parts = message.split("\\|", 4);
            if (parts.length != 4) {
                throw new IllegalArgumentException("Invalid order event message: " + message);
            }

            return new OrderEvent(parts[0], Long.valueOf(parts[1]), Long.valueOf(parts[2]), OrderStatus.valueOf(parts[3]));
        }
    }
}
