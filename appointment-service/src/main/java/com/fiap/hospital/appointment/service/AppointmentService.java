package com.fiap.hospital.appointment.service;

import com.fiap.hospital.appointment.dto.AppointmentEvent;
import com.fiap.hospital.appointment.dto.AppointmentRequest;
import com.fiap.hospital.appointment.dto.PatientHistory;
import com.fiap.hospital.appointment.entity.*;
import com.fiap.hospital.appointment.messaging.AppointmentEventPublisher;
import com.fiap.hospital.appointment.repository.AppointmentRepository;
import com.fiap.hospital.appointment.repository.DoctorRepository;
import com.fiap.hospital.appointment.repository.PatientRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentEventPublisher eventPublisher;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentEventPublisher eventPublisher
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.eventPublisher = eventPublisher;
    }

    public Appointment create(AppointmentRequest request) {
        var patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        var doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado"));

        var appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDateTime(request.dateTime());
        appointment.setStatus(request.status() == null ? AppointmentStatus.SCHEDULED : request.status());
        appointment.setNotes(request.notes());

        var saved = appointmentRepository.save(appointment);
        eventPublisher.publishCreated(toEvent(saved, "APPOINTMENT_CREATED"));
        return saved;
    }

    public Appointment update(Long id, AppointmentRequest request) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        if (request.patientId() != null) {
            appointment.setPatient(patientRepository.findById(request.patientId())
                    .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado")));
        }
        if (request.doctorId() != null) {
            appointment.setDoctor(doctorRepository.findById(request.doctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado")));
        }
        if (request.dateTime() != null) {
            appointment.setDateTime(request.dateTime());
        }
        if (request.status() != null) {
            appointment.setStatus(request.status());
        }
        if (request.notes() != null) {
            appointment.setNotes(request.notes());
        }

        var saved = appointmentRepository.save(appointment);
        eventPublisher.publishUpdated(toEvent(saved, "APPOINTMENT_UPDATED"));
        return saved;
    }

    public Appointment findById(Long id, Authentication authentication) {
        var appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
        ensureCanAccessPatient(appointment.getPatient().getId(), authentication);
        return appointment;
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPatient(Long patientId, Authentication authentication) {
        ensureCanAccessPatient(patientId, authentication);
        return appointmentRepository.findByPatientIdOrderByDateTimeDesc(patientId);
    }

    public List<Appointment> findFutureByPatient(Long patientId, Authentication authentication) {
        ensureCanAccessPatient(patientId, authentication);
        return appointmentRepository.findByPatientIdAndDateTimeAfterOrderByDateTimeAsc(patientId, LocalDateTime.now());
    }

    public PatientHistory getMedicalHistory(Long patientId, Authentication authentication) {
        ensureCanAccessPatient(patientId, authentication);
        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        return new PatientHistory(patient, appointmentRepository.findByPatientIdOrderByDateTimeDesc(patientId));
    }

    private void ensureCanAccessPatient(Long patientId, Authentication authentication) {
        if (hasRole(authentication, Role.ROLE_MEDICO.name()) || hasRole(authentication, Role.ROLE_ENFERMEIRO.name())) {
            return;
        }

        var currentPatient = patientRepository.findByUserEmail(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuário não é paciente"));

        if (!currentPatient.getId().equals(patientId)) {
            throw new AccessDeniedException("Paciente só pode acessar as próprias consultas");
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private AppointmentEvent toEvent(Appointment appointment, String eventType) {
        return new AppointmentEvent(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getPatient().getEmail(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getDateTime(),
                eventType
        );
    }
}
