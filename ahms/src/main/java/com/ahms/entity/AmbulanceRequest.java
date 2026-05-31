package com.ahms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "ambulance_requests")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AmbulanceRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne @JoinColumn(name = "patient_id") private Patient patient;

    @Column(nullable = false) private String requesterName;
    @Column(nullable = false) private String requesterPhone;
    @Column(nullable = false, columnDefinition = "TEXT") private String pickupAddress;
    private Double latitude;
    private Double longitude;
    private String emergencyType;

    @Enumerated(EnumType.STRING) @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private String assignedDriver;
    private String ambulanceNumber;
    private Integer estimatedArrival;
    @Column(columnDefinition = "TEXT") private String notes;
    private LocalDateTime requestedAt;
    private LocalDateTime dispatchedAt;

    @PrePersist protected void onCreate() { requestedAt = LocalDateTime.now(); }

    public enum RequestStatus { PENDING, DISPATCHED, EN_ROUTE, ARRIVED, COMPLETED, CANCELLED }
}
