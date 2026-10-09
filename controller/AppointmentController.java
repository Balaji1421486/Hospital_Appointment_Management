package com.task.hospital.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.hospital.entity.Appointment;
import com.task.hospital.service.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    AppointmentService as;

    @GetMapping("/all")
    public List<Appointment> getAll() {
        return as.getAllAppointments();
    }

    @GetMapping("/getByID/{id}")
    public Appointment getById(@PathVariable Long id) {
        return as.getAppointmentById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Appointment> add(@Valid @RequestBody Appointment a) {
        return ResponseEntity.status(HttpStatus.CREATED).body(as.addAppointment(a));
    }

    @PutMapping("/update/{id}")
    public Appointment update(@PathVariable Long id, @Valid @RequestBody Appointment a) {
        return as.updateAppointment(id, a);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        as.deleteAppointment(id);
        return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully"));
    }

    @PatchMapping("/patch/{id}")
    public Appointment patch(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return as.patchAppointment(id, updates);
    }

    @PostMapping("/addAll")
    public ResponseEntity<List<Appointment>> addAll(@RequestBody List<Appointment> list) {
        return ResponseEntity.status(HttpStatus.CREATED).body(as.addAll(list));
    }

    @GetMapping("/patient/{name}")
    public List<Appointment> byPatient(@PathVariable String name) {
        return as.getByPatient(name);
    }

    @GetMapping("/doctor/{name}")
    public List<Appointment> byDoctor(@PathVariable String name) {
        return as.getByDoctor(name);
    }

    @GetMapping("/disease/{disease}")
    public List<Appointment> byDisease(@PathVariable String disease) {
        return as.getByDisease(disease);
    }

    @GetMapping("/date/{date}")
    public List<Appointment> byDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return as.getByDate(date);
    }

    @GetMapping("/before/{date}")
    public List<Appointment> before(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return as.getBefore(date);
    }

    @GetMapping("/after/{date}")
    public List<Appointment> after(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return as.getAfter(date);
    }

    @GetMapping("/between")
    public List<Appointment> between(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return as.getBetween(from, to);
    }

    @GetMapping("/today")
    public List<Appointment> today() {
        return as.getToday();
    }

    @GetMapping("/upcoming")
    public List<Appointment> upcoming() {
        return as.getUpcoming();
    }

    @GetMapping("/search/patient")
    public List<Appointment> searchPatient(@RequestParam String keyword) {
        return as.searchPatient(keyword);
    }

    @GetMapping("/search/doctor")
    public List<Appointment> searchDoctor(@RequestParam String keyword) {
        return as.searchDoctor(keyword);
    }

    @GetMapping("/search/disease")
    public List<Appointment> searchDisease(@RequestParam String keyword) {
        return as.searchDisease(keyword);
    }

    @GetMapping("/sortByDate")
    public List<Appointment> sortByDate() {
        return as.sortByDate(true);
    }

    @GetMapping("/sortByDateDesc")
    public List<Appointment> sortByDateDesc() {
        return as.sortByDate(false);
    }

    @GetMapping("/sortByPatient")
    public List<Appointment> sortByPatient() {
        return as.sortByPatient();
    }

    @GetMapping("/page")
    public List<Appointment> page(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        return as.getPage(page, size);
    }

    @GetMapping("/count")
    public Map<String, Long> count() {
        return Map.of("totalAppointments", as.count());
    }

    @GetMapping("/count/doctor/{name}")
    public Map<String, Object> countByDoctor(@PathVariable String name) {
        return Map.of("doctorName", name, "count", as.countByDoctor(name));
    }

    @GetMapping("/count/date/{date}")
    public Map<String, Object> countByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return Map.of("date", date.toString(), "count", as.countByDate(date));
    }

    @GetMapping("/exists/{id}")
    public Map<String, Boolean> exists(@PathVariable Long id) {
        return Map.of("exists", as.exists(id));
    }

    @PutMapping("/reschedule/{id}")
    public Appointment reschedule(@PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return as.reschedule(id, date);
    }

    @DeleteMapping("/deleteByDoctor/{name}")
    public Map<String, Object> deleteByDoctor(@PathVariable String name) {
        return Map.of("message", "Appointments deleted", "deletedCount", as.deleteByDoctor(name));
    }

    @DeleteMapping("/deletePast")
    public Map<String, Object> deletePast() {
        return Map.of("message", "Past appointments deleted", "deletedCount", as.deletePast());
    }
}