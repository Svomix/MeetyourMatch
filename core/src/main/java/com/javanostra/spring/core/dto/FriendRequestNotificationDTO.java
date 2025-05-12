package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FriendRequestNotificationDTO implements NotificationDTO {
    private final String requesterName;

    @Override
    public String getTitle() {
        return "Новый запрос в друзья";
    }

    @Override
    public String getBody() {
        return requesterName + " хочет стать вашим другом";
    }
}
