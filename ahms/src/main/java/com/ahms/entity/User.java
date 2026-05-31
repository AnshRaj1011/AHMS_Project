package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) private String username;
    @Column(nullable = false) private String password;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "full_name", nullable = false) private String fullName;
    @Column(name = "phone_number") private String phoneNumber;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Role role;

    @Builder.Default @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }

    public enum Role { PATIENT, DOCTOR, ADMIN, EMERGENCY_STAFF }
}
