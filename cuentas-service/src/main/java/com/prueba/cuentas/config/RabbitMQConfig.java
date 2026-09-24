package com.prueba.cuentas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.messaging.exchange}")
    private String exchangeName;

    @Value("${app.messaging.queue}")
    private String queueName;

    @Value("${app.messaging.routing-key-pattern}")
    private String routingKeyPattern;

    @Bean
    public TopicExchange clientesExchange() {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Queue clientesQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding clientesBinding(Queue clientesQueue, TopicExchange clientesExchange) {
        return BindingBuilder.bind(clientesQueue).to(clientesExchange).with(routingKeyPattern);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
