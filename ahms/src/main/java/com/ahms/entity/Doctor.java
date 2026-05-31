package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "doctors")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne @JoinColumn(name = "user_id", nullable = false) private User user;

    @Column(nullable = false) private String specialization;
    private String qualification;
    @Column(name = "license_number", unique = true) private String licenseNumber;
    private Integer experienceYears;
    private Double consultationFee;
    private String availableDays;   // "MON,TUE,WED,THU,FRI"
    private String availableFrom;   // "09:00"
    private String availableTo;     // "17:00"
    private String cabinNumber;
    private String department;

    @Builder.Default private Boolean isAvailable = true;
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
