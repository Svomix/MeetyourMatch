package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp timestamp;
}
