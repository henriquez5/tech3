package com.fiap.hospital.appointment.repository;

import com.fiap.hospital.appointment.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
