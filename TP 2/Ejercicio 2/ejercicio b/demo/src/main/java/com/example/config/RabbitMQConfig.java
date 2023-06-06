package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "image-queue";
    private static final String exchangeName = "my-image-exchange";
    private static final String routingKey = "image-routing-key";

    @Bean
    public Queue imageQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange imageExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(imageQueue()).to(imageExchange()).with(routingKey);
    }

}
