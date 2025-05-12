package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_firebase_token")
@IdClass(UserFirebaseTokenId.class)
@Data
@NoArgsConstructor
public class UserFirebaseToken {
    @Id
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Id
    private String deviceId;
    private LocalDateTime lastUpdated;
    private String firebaseToken;

    public UserFirebaseToken(User user, String deviceId, LocalDateTime lastUpdated, String firebaseToken) {
        this.user = user;
        this.deviceId = deviceId;
        this.lastUpdated = lastUpdated;
        this.firebaseToken = firebaseToken;
    }
}
