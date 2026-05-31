package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "icu_requests")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class IcuRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name = "patient_id", nullable = false) private Patient patient;

    @Column(nullable = false, columnDefinition = "TEXT") private String reason;
    private String medicalCondition;

    @Enumerated(EnumType.STRING) @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING) @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne @JoinColumn(name = "assigned_bed_id") private Bed assignedBed;

    private String adminNotes;
    @Column(name = "requested_at") private LocalDateTime requestedAt;
    @Column(name = "resolved_at") private LocalDateTime resolvedAt;

    @PrePersist protected void onCreate() { requestedAt = LocalDateTime.now(); }

    public enum Priority      { LOW, MEDIUM, HIGH, CRITICAL }
    public enum RequestStatus { PENDING, APPROVED, REJECTED, ADMITTED }
}
