package com.javanostra.spring.core.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotificationDTO {
    private Long id;
    private String senderId;
    private String recipientId;
    private String content;
    private Timestamp timestamp;
}
