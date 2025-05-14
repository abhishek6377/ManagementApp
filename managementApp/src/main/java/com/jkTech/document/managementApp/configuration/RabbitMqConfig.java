package com.jkTech.document.managementApp.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String DOCUMENT_QUEUE = "document_queue";
    public static final String QQ_QUEUE = "qq_queue";
    public static final String EXCHANGE = "document_exchange";
    public static final String DOCUMENT_ROUTING_KEY = "document_routing";
    public static final String QQ_ROUTING_KEY = "qq_routing";
    public static final String QA_ROUTING_KEY = "qa_routing";

    @Bean
    public Queue documentQueue() {
        return new Queue(DOCUMENT_QUEUE);
    }

    @Bean
    public Queue qqQueue() {
        return new Queue(QQ_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding documentBinding() {
        return BindingBuilder
                .bind(documentQueue())
                .to(exchange())
                .with(DOCUMENT_ROUTING_KEY);
    }

    @Bean
    public Binding qqBinding() {
        return BindingBuilder
                .bind(qqQueue())
                .to(exchange())
                .with(QQ_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter((MessageConverter) converter());
        return rabbitTemplate;
    }
}
