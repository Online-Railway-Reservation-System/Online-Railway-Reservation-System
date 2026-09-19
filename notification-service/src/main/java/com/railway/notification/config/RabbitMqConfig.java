package com.railway.notification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "railway.reservation.exchange";
    public static final String RESERVATION_QUEUE = "railway.notification.reservation.queue";
    public static final String PAYMENT_QUEUE = "railway.notification.payment.queue";
    public static final String REFUND_QUEUE = "railway.notification.refund.queue";
    public static final String FOOD_QUEUE = "railway.notification.food.queue";

    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue reservationQueue() {
        return new Queue(RESERVATION_QUEUE, true);
    }

    @Bean
    public Queue paymentQueue() {
        return new Queue(PAYMENT_QUEUE, true);
    }

    @Bean
    public Queue refundQueue() {
        return new Queue(REFUND_QUEUE, true);
    }

    @Bean
    public Queue foodQueue() {
        return new Queue(FOOD_QUEUE, true);
    }

    @Bean
    public Binding reservationBinding(Queue reservationQueue, TopicExchange reservationExchange) {
        return BindingBuilder.bind(reservationQueue).to(reservationExchange).with("railway.reservation.*");
    }

    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange reservationExchange) {
        return BindingBuilder.bind(paymentQueue).to(reservationExchange).with("railway.payment.*");
    }

    @Bean
    public Binding refundBinding(Queue refundQueue, TopicExchange reservationExchange) {
        return BindingBuilder.bind(refundQueue).to(reservationExchange).with("railway.refund.*");
    }

    @Bean
    public Binding foodBinding(Queue foodQueue, TopicExchange reservationExchange) {
        return BindingBuilder.bind(foodQueue).to(reservationExchange).with("railway.food.*");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
