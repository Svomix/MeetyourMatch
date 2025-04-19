package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "calls")
@Data
@NoArgsConstructor
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callId;
    private String callerId;
    private String calleeId;
    private Instant startedAt;
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    private CallStatus status;

    public enum CallStatus {
        ACTIVE, ENDED
    }
}