package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @Table(name = "appointments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name = "patient_id", nullable = false) private Patient patient;
    @ManyToOne @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;

    @Column(nullable = false) private LocalDate appointmentDate;
    @Column(nullable = false) private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING) @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Enumerated(EnumType.STRING) @Builder.Default
    private AppointmentType type = AppointmentType.IN_PERSON;

    @Column(columnDefinition = "TEXT") private String reason;
    @Column(columnDefinition = "TEXT") private String doctorNotes;
    @Column(columnDefinition = "TEXT") private String prescription;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate()  { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate()  { updatedAt = LocalDateTime.now(); }

    public enum AppointmentStatus { PENDING, CONFIRMED, COMPLETED, CANCELLED, NO_SHOW }
    public enum AppointmentType   { IN_PERSON, VIRTUAL }
}
