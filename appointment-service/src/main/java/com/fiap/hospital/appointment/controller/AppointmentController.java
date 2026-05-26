package com.fiap.hospital.appointment.controller;

import com.fiap.hospital.appointment.dto.AppointmentRequest;
import com.fiap.hospital.appointment.entity.Appointment;
import com.fiap.hospital.appointment.service.AppointmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO','ROLE_ENFERMEIRO')")
    public Appointment create(@RequestBody AppointmentRequest request) {
        return appointmentService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MEDICO')")
    public Appointment update(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        return appointmentService.update(id, request);
    }

    @GetMapping("/{id}")
    public Appointment findById(@PathVariable Long id, Authentication authentication) {
        return appointmentService.findById(id, authentication);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MEDICO','ROLE_ENFERMEIRO')")
    public List<Appointment> findAll() {
        return appointmentService.findAll();
    }
}
