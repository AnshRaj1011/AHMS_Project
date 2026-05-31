package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "beds")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Bed {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(unique = true, nullable = false) private String bedNumber;
    @Column(nullable = false) private String wardName;

    @Enumerated(EnumType.STRING) @Column(nullable = false) private BedType bedType;
    @Enumerated(EnumType.STRING) @Builder.Default private BedStatus status = BedStatus.AVAILABLE;

    @ManyToOne @JoinColumn(name = "current_patient_id") private Patient currentPatient;

    private LocalDateTime admittedAt;
    private Double dailyRate;
    private Integer floorNumber;

    public enum BedType   { GENERAL, ICU, EMERGENCY, PRIVATE, SEMI_PRIVATE }
    public enum BedStatus { AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED }
}
