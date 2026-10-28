package com.rutaexpress.notify.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de mensajería: 3 exchanges, 3 colas de trabajo con su DLQ y los bindings direct y topic.
 * Los mismos nombres están en infra/rabbitmq/definitions.json.
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class RabbitConfig {

    @Bean
    DirectExchange cmdDirectExchange(RabbitProperties p) {
        return new DirectExchange(p.exchanges().direct(), true, false);
    }

    @Bean
    TopicExchange cmdTopicExchange(RabbitProperties p) {
        return new TopicExchange(p.exchanges().topic(), true, false);
    }

    @Bean
    TopicExchange cmdDeadExchange(RabbitProperties p) {
        return new TopicExchange(p.exchanges().dead(), true, false);
    }

    @Bean
    Queue emailQueue(RabbitProperties p) {
        return workQueue(p, p.queues().email());
    }

    @Bean
    Queue warehouseQueue(RabbitProperties p) {
        return workQueue(p, p.queues().warehouse());
    }

    @Bean
    Queue labelQueue(RabbitProperties p) {
        return workQueue(p, p.queues().label());
    }

    @Bean
    Queue emailDlq(RabbitProperties p) {
        return QueueBuilder.durable(p.queues().email() + p.queues().dlqSuffix()).build();
    }

    @Bean
    Queue warehouseDlq(RabbitProperties p) {
        return QueueBuilder.durable(p.queues().warehouse() + p.queues().dlqSuffix()).build();
    }

    @Bean
    Queue labelDlq(RabbitProperties p) {
        return QueueBuilder.durable(p.queues().label() + p.queues().dlqSuffix()).build();
    }

    @Bean
    Binding emailDirectBinding(Queue emailQueue, DirectExchange cmdDirectExchange, RabbitProperties p) {
        return BindingBuilder.bind(emailQueue).to(cmdDirectExchange).with(p.routing().emailSend());
    }

    @Bean
    Binding warehouseDirectBinding(Queue warehouseQueue, DirectExchange cmdDirectExchange, RabbitProperties p) {
        return BindingBuilder.bind(warehouseQueue).to(cmdDirectExchange).with(p.routing().warehouseTicket());
    }

    @Bean
    Binding labelDirectBinding(Queue labelQueue, DirectExchange cmdDirectExchange, RabbitProperties p) {
        return BindingBuilder.bind(labelQueue).to(cmdDirectExchange).with(p.routing().labelGen());
    }

    @Bean
    Binding emailTopicBinding(Queue emailQueue, TopicExchange cmdTopicExchange, RabbitProperties p) {
        return BindingBuilder.bind(emailQueue).to(cmdTopicExchange).with(p.routing().emailPattern());
    }

    @Bean
    Binding warehouseTopicBinding(Queue warehouseQueue, TopicExchange cmdTopicExchange, RabbitProperties p) {
        return BindingBuilder.bind(warehouseQueue).to(cmdTopicExchange).with(p.routing().warehousePattern());
    }

    @Bean
    Binding labelTopicBinding(Queue labelQueue, TopicExchange cmdTopicExchange, RabbitProperties p) {
        return BindingBuilder.bind(labelQueue).to(cmdTopicExchange).with(p.routing().labelPattern());
    }

    @Bean
    Binding emailDlqBinding(Queue emailDlq, TopicExchange cmdDeadExchange, RabbitProperties p) {
        return BindingBuilder.bind(emailDlq).to(cmdDeadExchange).with(p.routing().emailDead());
    }

    @Bean
    Binding warehouseDlqBinding(Queue warehouseDlq, TopicExchange cmdDeadExchange, RabbitProperties p) {
        return BindingBuilder.bind(warehouseDlq).to(cmdDeadExchange).with(p.routing().warehouseDead());
    }

    @Bean
    Binding labelDlqBinding(Queue labelDlq, TopicExchange cmdDeadExchange, RabbitProperties p) {
        return BindingBuilder.bind(labelDlq).to(cmdDeadExchange).with(p.routing().labelDead());
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    private Queue workQueue(RabbitProperties p, String name) {
        return QueueBuilder.durable(name).deadLetterExchange(p.exchanges().dead()).build();
    }
}
