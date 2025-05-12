package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NewMessageNotificationDTO implements NotificationDTO {
    private final String senderName;
    private final String message;

    @Override
    public String getTitle() {
        return "Новое сообщение от " + senderName;
    }

    @Override
    public String getBody() {
        return message;
    }
}
