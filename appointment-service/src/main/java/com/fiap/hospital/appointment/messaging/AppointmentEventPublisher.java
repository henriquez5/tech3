package com.fiap.hospital.appointment.messaging;

import com.fiap.hospital.appointment.dto.AppointmentEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String createdRoutingKey;
    private final String updatedRoutingKey;

    public AppointmentEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange}") String exchange,
            @Value("${app.rabbitmq.created-routing-key}") String createdRoutingKey,
            @Value("${app.rabbitmq.updated-routing-key}") String updatedRoutingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.createdRoutingKey = createdRoutingKey;
        this.updatedRoutingKey = updatedRoutingKey;
    }

    public void publishCreated(AppointmentEvent event) {
        rabbitTemplate.convertAndSend(exchange, createdRoutingKey, event);
    }

    public void publishUpdated(AppointmentEvent event) {
        rabbitTemplate.convertAndSend(exchange, updatedRoutingKey, event);
    }
}
