package com.fiap.hospital.appointment.dto;

import java.time.LocalDateTime;

public record AppointmentEvent(
        Long appointmentId,
        Long patientId,
        String patientName,
        String patientEmail,
        Long doctorId,
        String doctorName,
        LocalDateTime appointmentDate,
        String eventType
) {}
