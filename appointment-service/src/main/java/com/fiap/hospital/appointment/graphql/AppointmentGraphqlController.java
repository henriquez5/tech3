package com.fiap.hospital.appointment.graphql;

import com.fiap.hospital.appointment.dto.AppointmentRequest;
import com.fiap.hospital.appointment.dto.PatientHistory;
import com.fiap.hospital.appointment.entity.Appointment;
import com.fiap.hospital.appointment.service.AppointmentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentGraphqlController {
    private final AppointmentService appointmentService;

    public AppointmentGraphqlController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @QueryMapping
    public Appointment appointmentById(@Argument Long id, Authentication authentication) {
        return appointmentService.findById(id, authentication);
    }

    @QueryMapping
    public List<Appointment> appointmentsByPatient(@Argument Long patientId, Authentication authentication) {
        return appointmentService.findByPatient(patientId, authentication);
    }

    @QueryMapping
    public List<Appointment> futureAppointmentsByPatient(@Argument Long patientId, Authentication authentication) {
        return appointmentService.findFutureByPatient(patientId, authentication);
    }

    @QueryMapping
    public PatientHistory medicalHistory(@Argument Long patientId, Authentication authentication) {
        return appointmentService.getMedicalHistory(patientId, authentication);
    }

    @MutationMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO','ROLE_ENFERMEIRO')")
    public Appointment createAppointment(@Argument AppointmentInput input) {
        return appointmentService.create(input.toRequest());
    }

    @MutationMapping
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public Appointment updateAppointment(@Argument Long id, @Argument AppointmentInput input) {
        return appointmentService.update(id, input.toRequest());
    }

    public record AppointmentInput(Long patientId, Long doctorId, String dateTime, String status, String notes) {
        public AppointmentRequest toRequest() {
            return new AppointmentRequest(
                    patientId,
                    doctorId,
                    dateTime == null ? null : java.time.LocalDateTime.parse(dateTime),
                    status == null ? null : com.fiap.hospital.appointment.entity.AppointmentStatus.valueOf(status),
                    notes
            );
        }
    }
}
