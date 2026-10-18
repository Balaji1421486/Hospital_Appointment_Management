
package com.balaji.hospitalappointment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository repository;

    public AppointmentController(AppointmentRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Appointment> addAppointment(
            @Valid @RequestBody Appointment appointment) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(appointment));
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(
            @PathVariable Long id) {

        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody Appointment details) {

        return repository.findById(id).map(appointment -> {
            appointment.setPatientName(details.getPatientName());
            appointment.setDoctorName(details.getDoctorName());
            appointment.setAppointmentDate(details.getAppointmentDate());
            appointment.setAppointmentTime(details.getAppointmentTime());
            appointment.setReason(details.getReason());

            Appointment updated = repository.save(appointment);
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @PathVariable Long id) {

        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
