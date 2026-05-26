package com.fiap.hospital.notification.consumer;

import com.fiap.hospital.notification.dto.AppointmentEvent;
import com.fiap.hospital.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(AppointmentEventConsumer.class);
    private final NotificationService notificationService;

    public AppointmentEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${app.rabbitmq.notification-queue}")
    public void consume(AppointmentEvent event) {
        log.info("Evento recebido do RabbitMQ: {}", event);
        notificationService.sendAppointmentReminder(event);
    }
}
