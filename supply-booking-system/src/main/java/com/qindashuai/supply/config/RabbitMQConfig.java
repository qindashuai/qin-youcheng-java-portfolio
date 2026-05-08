package com.qindashuai.supply.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUALIFICATION_WARNING_QUEUE = "qualification.warning";
    public static final String QUALIFICATION_WARNING_EXCHANGE = "qualification.warning.exchange";
    public static final String QUALIFICATION_WARNING_ROUTING_KEY = "qualification.warning.route";

    public static final String BOOKING_LINKAGE_QUEUE = "booking.linkage";
    public static final String BOOKING_LINKAGE_EXCHANGE = "booking.linkage.exchange";
    public static final String BOOKING_LINKAGE_ROUTING_KEY = "booking.linkage.route";

    @Bean
    public Queue qualificationWarningQueue() {
        return new Queue(QUALIFICATION_WARNING_QUEUE, true);
    }

    @Bean
    public DirectExchange qualificationWarningExchange() {
        return new DirectExchange(QUALIFICATION_WARNING_EXCHANGE, true, false);
    }

    @Bean
    public Binding qualificationWarningBinding() {
        return BindingBuilder.bind(qualificationWarningQueue())
                .to(qualificationWarningExchange())
                .with(QUALIFICATION_WARNING_ROUTING_KEY);
    }

    @Bean
    public Queue bookingLinkageQueue() {
        return new Queue(BOOKING_LINKAGE_QUEUE, true);
    }

    @Bean
    public DirectExchange bookingLinkageExchange() {
        return new DirectExchange(BOOKING_LINKAGE_EXCHANGE, true, false);
    }

    @Bean
    public Binding bookingLinkageBinding() {
        return BindingBuilder.bind(bookingLinkageQueue())
                .to(bookingLinkageExchange())
                .with(BOOKING_LINKAGE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
