package com.fiap.hospital.notification.service;

import com.fiap.hospital.notification.dto.AppointmentEvent;
import com.fiap.hospital.notification.entity.NotificationLog;
import com.fiap.hospital.notification.entity.NotificationStatus;
import com.fiap.hospital.notification.repository.NotificationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationLogRepository repository;

    public NotificationService(NotificationLogRepository repository) {
        this.repository = repository;
    }

    public void sendAppointmentReminder(AppointmentEvent event) {
        var message = "Olá, " + event.patientName()
                + ". Sua consulta com " + event.doctorName()
                + " está agendada para " + event.appointmentDate() + ".";

        log.info("Simulando envio de lembrete para {}: {}", event.patientEmail(), message);

        var logEntity = new NotificationLog();
        logEntity.setAppointmentId(event.appointmentId());
        logEntity.setPatientId(event.patientId());
        logEntity.setPatientEmail(event.patientEmail());
        logEntity.setMessage(message);
        logEntity.setStatus(NotificationStatus.SENT);
        logEntity.setEventType(event.eventType());
        repository.save(logEntity);
    }
}
