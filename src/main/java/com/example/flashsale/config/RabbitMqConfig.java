package com.example.flashsale.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue}")
    private String queue;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${app.rabbitmq.dead-letter-exchange}")
    private String deadLetterExchange;

    @Value("${app.rabbitmq.dead-letter-routing-key}")
    private String deadLetterRoutingKey;

    @Bean
    public Queue flashSaleQueue() {
        return QueueBuilder.durable(queue)
                .deadLetterExchange(deadLetterExchange)
                .deadLetterRoutingKey(deadLetterRoutingKey)
                .build();
    }

    @Bean
    public TopicExchange flashSaleExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public DirectExchange flashSaleDeadLetterExchange() {
        return new DirectExchange(deadLetterExchange, true, false);
    }

    @Bean
    public Binding flashSaleBinding(@Qualifier("flashSaleQueue") Queue flashSaleQueue,
            TopicExchange flashSaleExchange) {
        return BindingBuilder.bind(flashSaleQueue).to(flashSaleExchange).with(routingKey);
    }

    @Bean
    public Queue flashSaleDeadLetterQueue() {
        return QueueBuilder.durable(queue + ".dlq").build();
    }

    @Bean
    public Binding flashSaleDeadLetterBinding(@Qualifier("flashSaleDeadLetterQueue") Queue flashSaleDeadLetterQueue,
            DirectExchange flashSaleDeadLetterExchange) {
        return BindingBuilder.bind(flashSaleDeadLetterQueue).to(flashSaleDeadLetterExchange)
                .with(deadLetterRoutingKey);
    }

}
