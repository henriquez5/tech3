package com.fiap.hospital.appointment.dto;

import com.fiap.hospital.appointment.entity.Appointment;
import com.fiap.hospital.appointment.entity.Patient;
import java.util.List;

public record PatientHistory(Patient patient, List<Appointment> appointments) {}
