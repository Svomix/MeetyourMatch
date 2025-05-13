package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NewGroupMessageNotificationDTO implements NotificationDTO {
    private final String senderName;
    private final String groupName;
    private final String message;

    @Override
    public String getTitle() {
        return "Новое сообщение в группе " + groupName + " от " + senderName;
    }

    @Override
    public String getBody() {
        return message;
    }
}
