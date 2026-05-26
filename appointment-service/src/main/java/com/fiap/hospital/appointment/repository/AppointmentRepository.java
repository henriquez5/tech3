package com.fiap.hospital.appointment.repository;

import com.fiap.hospital.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByDateTimeDesc(Long patientId);
    List<Appointment> findByPatientIdAndDateTimeAfterOrderByDateTimeAsc(Long patientId, LocalDateTime dateTime);
}
