package com.javanostra.meetyourmatch.persistance.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserFirebaseTokenDTO {
    private Long userId;
    private String deviceId;
    private String firebaseToken;

    public UserFirebaseTokenDTO(Long userId, String deviceId, String firebaseToken) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.firebaseToken = firebaseToken;
    }
}
