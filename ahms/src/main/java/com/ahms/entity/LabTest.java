package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "lab_tests")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class LabTest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name = "patient_id", nullable = false) private Patient patient;

    @Column(nullable = false) private String testName;

    @Enumerated(EnumType.STRING) private TestCategory testCategory;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING) @Builder.Default
    private LabTestStatus status = LabTestStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT") private String result;
    private String reportPath;
    private Double price;
    private String orderedBy;
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum TestCategory  { BLOOD_TEST, CT_SCAN, MRI, XRAY, URINE_TEST, ECG, ULTRASOUND, OTHER }
    public enum LabTestStatus { SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }
}
