package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "patients")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @OneToOne @JoinColumn(name = "user_id", nullable = false) private User user;

    @Column(name = "registration_id", unique = true, nullable = false)
    private String registrationId;

    @Column(name = "qr_code_path") private String qrCodePath;
    @Column(name = "date_of_birth") private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING) private Gender gender;

    private String bloodGroup;
    @Column(columnDefinition = "TEXT") private String address;
    private String emergencyContact;
    @Column(columnDefinition = "TEXT") private String medicalHistory;
    @Column(columnDefinition = "TEXT") private String allergies;

    @Column(name = "registered_at") private LocalDateTime registeredAt;

    @PrePersist protected void onCreate() { registeredAt = LocalDateTime.now(); }

    public enum Gender { MALE, FEMALE, OTHER }
}
