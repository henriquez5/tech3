package com.fiap.hospital.appointment.dto;

import com.fiap.hospital.appointment.entity.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentRequest(
        Long patientId,
        Long doctorId,
        LocalDateTime dateTime,
        AppointmentStatus status,
        String notes
) {}
