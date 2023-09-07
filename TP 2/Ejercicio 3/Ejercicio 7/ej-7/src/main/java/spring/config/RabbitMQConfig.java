package spring.config;

import javax.annotation.PostConstruct;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "task-queue";
    private static final String exchangeName = "my-task-exchange";
    private static final String routingKey = "task-routing-key";

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

    @Autowired
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void initializeQueue() {
        Queue imageQueue = new Queue(QUEUE_NAME, true);
        DirectExchange imageExchange = new DirectExchange(exchangeName);
        Binding binding = BindingBuilder.bind(imageQueue).to(imageExchange).with(routingKey);
        amqpAdmin.declareQueue(imageQueue);
        amqpAdmin.declareExchange(imageExchange);
        amqpAdmin.declareBinding(binding);
    }

}

