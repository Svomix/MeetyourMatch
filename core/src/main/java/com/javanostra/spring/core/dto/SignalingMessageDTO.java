package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignalingMessageDTO {
    private String id;
    private String type;
    private String senderId;
    private String recipientId;
    private Object data;
}
