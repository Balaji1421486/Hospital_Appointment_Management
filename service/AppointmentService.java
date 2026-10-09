package com.task.hospital.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.hospital.entity.Appointment;
import com.task.hospital.exception.ResourceNotFoundException;
import com.task.hospital.repository.AppointmentRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class AppointmentService {

    @Autowired
    AppointmentRepository ar;

    @Autowired
    Validator va;

    public List<Appointment> getAllAppointments() {
        return ar.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return ar.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    public Appointment addAppointment(Appointment a) {
        a.setAppointmentId(null);
        return ar.save(a);
    }

    public Appointment updateAppointment(Long id, Appointment newData) {
        Appointment existing = getAppointmentById(id);
        existing.setPatientName(newData.getPatientName());
        existing.setDoctorName(newData.getDoctorName());
        existing.setDisease(newData.getDisease());
        existing.setAppointmentDate(newData.getAppointmentDate());
        return ar.save(existing);
    }

    public void deleteAppointment(Long id) {
        ar.delete(getAppointmentById(id));
    }

    public Appointment patchAppointment(Long id, Map<String, Object> updates) {
        Appointment existing = getAppointmentById(id);

        if (updates.containsKey("patientName")) {
            String v = (String) updates.get("patientName");
            if (v == null || v.isBlank()) {
                throw new IllegalArgumentException("Patient name is required");
            }
            existing.setPatientName(v);
        }
        if (updates.containsKey("doctorName")) {
            String v = (String) updates.get("doctorName");
            if (v == null || v.isBlank()) {
                throw new IllegalArgumentException("Doctor name is required");
            }
            existing.setDoctorName(v);
        }
        if (updates.containsKey("disease")) {
            String v = (String) updates.get("disease");
            if (v == null || v.isBlank()) {
                throw new IllegalArgumentException("Disease field cannot be empty");
            }
            existing.setDisease(v);
        }
        if (updates.containsKey("appointmentDate")) {
            Object v = updates.get("appointmentDate");
            if (v == null) {
                throw new IllegalArgumentException("Appointment date is required");
            }
            LocalDate date;
            try {
                date = LocalDate.parse(v.toString());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Appointment date must be in yyyy-MM-dd format");
            }
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Appointment date cannot be in the past");
            }
            existing.setAppointmentDate(date);
        }
        return ar.save(existing);
    }

    @Transactional
    public List<Appointment> addAll(List<Appointment> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Appointment list cannot be empty");
        }
        for (int i = 0; i < list.size(); i++) {
            Appointment a = list.get(i);
            Set<ConstraintViolation<Appointment>> violations = va.validate(a);
            if (!violations.isEmpty()) {
                String msg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("Record " + (i + 1) + ": " + msg);
            }
            a.setAppointmentId(null);
        }
        return ar.saveAll(list);
    }

    public List<Appointment> getByPatient(String name) {
        return ar.findByPatientNameIgnoreCase(name);
    }

    public List<Appointment> getByDoctor(String name) {
        return ar.findByDoctorNameIgnoreCase(name);
    }

    public List<Appointment> getByDisease(String disease) {
        return ar.findByDiseaseIgnoreCase(disease);
    }

    public List<Appointment> getByDate(LocalDate date) {
        return ar.findByAppointmentDate(date);
    }

    public List<Appointment> getBefore(LocalDate date) {
        return ar.findByAppointmentDateBefore(date);
    }

    public List<Appointment> getAfter(LocalDate date) {
        return ar.findByAppointmentDateAfter(date);
    }

    public List<Appointment> getBetween(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date cannot be after 'to' date");
        }
        return ar.findByAppointmentDateBetween(from, to);
    }

    public List<Appointment> getToday() {
        return ar.findByAppointmentDate(LocalDate.now());
    }

    public List<Appointment> getUpcoming() {
        return ar.findByAppointmentDateGreaterThanEqual(LocalDate.now());
    }

    public List<Appointment> searchPatient(String keyword) {
        return ar.findByPatientNameContainingIgnoreCase(keyword);
    }

    public List<Appointment> searchDoctor(String keyword) {
        return ar.findByDoctorNameContainingIgnoreCase(keyword);
    }

    public List<Appointment> searchDisease(String keyword) {
        return ar.findByDiseaseContainingIgnoreCase(keyword);
    }

    public List<Appointment> sortByDate(boolean ascending) {
        Sort sort = ascending ? Sort.by("appointmentDate").ascending()
                              : Sort.by("appointmentDate").descending();
        return ar.findAll(sort);
    }

    public List<Appointment> sortByPatient() {
        return ar.findAll(Sort.by("patientName").ascending());
    }

    public List<Appointment> getPage(int page, int size) {
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException("page must be >= 0 and size must be >= 1");
        }
        return ar.findAll(PageRequest.of(page, size, Sort.by("appointmentDate"))).getContent();
    }

    public long count() {
        return ar.count();
    }

    public long countByDoctor(String name) {
        return ar.countByDoctorNameIgnoreCase(name);
    }

    public long countByDate(LocalDate date) {
        return ar.countByAppointmentDate(date);
    }

    public boolean exists(Long id) {
        return ar.existsById(id);
    }

    public Appointment reschedule(Long id, LocalDate newDate) {
        if (newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        Appointment existing = getAppointmentById(id);
        existing.setAppointmentDate(newDate);
        return ar.save(existing);
    }

    @Transactional
    public long deleteByDoctor(String name) {
        return ar.deleteByDoctorNameIgnoreCase(name);
    }

    @Transactional
    public long deletePast() {
        return ar.deleteByAppointmentDateBefore(LocalDate.now());
    }
}