package com.ahms.service;

import com.ahms.entity.*;
import com.ahms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service @RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;

    @Transactional
    public Appointment book(Long patientId, Long doctorId, LocalDate date, LocalTime time,
                             String reason, Appointment.AppointmentType type) {
        Patient patient = patientRepo.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor   = doctorRepo.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
        if (!repo.findConflicts(doctor, date, time).isEmpty())
            throw new RuntimeException("Slot not available. Please choose another time.");
        return repo.save(Appointment.builder().patient(patient).doctor(doctor)
                .appointmentDate(date).appointmentTime(time).reason(reason)
                .type(type != null ? type : Appointment.AppointmentType.IN_PERSON).build());
    }

    public List<Appointment> getByPatient(Long patientId) {
        return repo.findByPatient(patientRepo.findById(patientId).orElseThrow());
    }
    public List<Appointment> getByDoctor(Long doctorId) {
        return repo.findByDoctor(doctorRepo.findById(doctorId).orElseThrow());
    }
    public List<Appointment> getToday() { return repo.findByAppointmentDate(LocalDate.now()); }
    public List<Appointment> getAll()   { return repo.findAll(); }

    @Transactional
    public Appointment updateStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment a = repo.findById(id).orElseThrow();
        a.setStatus(status); return repo.save(a);
    }

    @Transactional
    public Appointment addNotes(Long id, String notes, String prescription) {
        Appointment a = repo.findById(id).orElseThrow();
        a.setDoctorNotes(notes); a.setPrescription(prescription);
        a.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return repo.save(a);
    }

    public long countToday() { return repo.countByDate(LocalDate.now()); }
}
