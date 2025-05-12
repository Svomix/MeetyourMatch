package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserFirebaseTokenDTO {
    private Long userId;
    private String deviceId;
    private String firebaseToken;
}
