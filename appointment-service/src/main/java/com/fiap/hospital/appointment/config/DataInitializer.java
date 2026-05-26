package com.fiap.hospital.appointment.config;

import com.fiap.hospital.appointment.entity.*;
import com.fiap.hospital.appointment.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            var doctorUser = userRepository.save(new User("Dr. João Silva", "medico@hospital.com", passwordEncoder.encode("senha123"), Role.ROLE_MEDICO));
            var nurseUser = userRepository.save(new User("Enfermeira Maria", "enfermeiro@hospital.com", passwordEncoder.encode("senha123"), Role.ROLE_ENFERMEIRO));
            var patientUser = userRepository.save(new User("Paciente Ana", "paciente@hospital.com", passwordEncoder.encode("senha123"), Role.ROLE_PACIENTE));

            userRepository.save(new User("Paciente Carlos", "paciente2@hospital.com", passwordEncoder.encode("senha123"), Role.ROLE_PACIENTE));

            var doctor = doctorRepository.save(new Doctor("Dr. João Silva", "CRM-12345", "Cardiologia", doctorUser));
            var patient = patientRepository.save(new Patient("Paciente Ana", "paciente@hospital.com", "11999990000", LocalDate.of(1995, 3, 10), patientUser));

            var appointment1 = new Appointment();
            appointment1.setPatient(patient);
            appointment1.setDoctor(doctor);
            appointment1.setDateTime(LocalDateTime.now().plusDays(2));
            appointment1.setStatus(AppointmentStatus.SCHEDULED);
            appointment1.setNotes("Consulta inicial de rotina.");
            appointmentRepository.save(appointment1);

            var appointment2 = new Appointment();
            appointment2.setPatient(patient);
            appointment2.setDoctor(doctor);
            appointment2.setDateTime(LocalDateTime.now().minusDays(10));
            appointment2.setStatus(AppointmentStatus.COMPLETED);
            appointment2.setNotes("Paciente relatou melhora após medicação.");
            appointmentRepository.save(appointment2);
        };
    }
}
