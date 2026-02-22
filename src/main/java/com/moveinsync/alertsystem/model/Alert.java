package com.moveinsync.alertsystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alertId;

    private String driverId;
    private String sourceType;   // overspeed, feedback, compliance
    private String severity;     // INFO, WARNING, CRITICAL

    private LocalDateTime timestamp;

    private String status;       // OPEN, ESCALATED, AUTO_CLOSED, RESOLVED

    @Builder.Default
    @Column(name = "escalation_level")
    private Integer escalationLevel = 0;   // ONLY ONE FIELD — INTEGER

    @Column(columnDefinition = "TEXT")
    private String metadata;
}