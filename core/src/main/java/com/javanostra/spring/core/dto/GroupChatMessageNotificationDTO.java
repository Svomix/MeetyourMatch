package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMessageNotificationDTO {
    private Long id;
    private Long groupChatId;
    private Long senderId;
    private String senderUsername;
    private String senderAvatarPath;
    private String content;
    private Timestamp timestamp;
}
