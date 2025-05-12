package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FriendAcceptNotificationDTO implements NotificationDTO {
    private final String friendName;

    @Override
    public String getTitle() {
        return "Ваша заявка в друзья принята";
    }

    @Override
    public String getBody() {
        return friendName + " принял вашу заявку в друзья";
    }
}
