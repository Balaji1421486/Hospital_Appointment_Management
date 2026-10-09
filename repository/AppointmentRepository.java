package com.task.hospital.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.hospital.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientNameIgnoreCase(String patientName);

    List<Appointment> findByDoctorNameIgnoreCase(String doctorName);

    List<Appointment> findByDiseaseIgnoreCase(String disease);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBefore(LocalDate date);

    List<Appointment> findByAppointmentDateAfter(LocalDate date);

    List<Appointment> findByAppointmentDateBetween(LocalDate from, LocalDate to);

    List<Appointment> findByAppointmentDateGreaterThanEqual(LocalDate date);

    List<Appointment> findByPatientNameContainingIgnoreCase(String keyword);

    List<Appointment> findByDoctorNameContainingIgnoreCase(String keyword);

    List<Appointment> findByDiseaseContainingIgnoreCase(String keyword);

    long countByDoctorNameIgnoreCase(String doctorName);

    long countByAppointmentDate(LocalDate date);

    long deleteByDoctorNameIgnoreCase(String doctorName);

    long deleteByAppointmentDateBefore(LocalDate date);
}