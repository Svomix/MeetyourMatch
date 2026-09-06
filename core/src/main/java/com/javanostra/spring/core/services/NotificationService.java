package com.javanostra.spring.core.services;

import com.google.firebase.messaging.*;
import com.javanostra.spring.core.dao.UserFirebaseTokenDAO;
import com.javanostra.spring.core.dto.NotificationDTO;
import com.javanostra.spring.core.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final UserFirebaseTokenDAO userFirebaseTokenDAO;

    public void sendNotification(NotificationDTO notificationDTO, User recipient) {
        Notification notification = Notification.builder()
                .setTitle(notificationDTO.getTitle())
                .setBody(notificationDTO.getBody())
                .build();

        List<Message> messages = userFirebaseTokenDAO
                .findByUserId(recipient.getId()).stream()
                .map(token -> Message.builder()
                                    .setToken(token.getFirebaseToken())
                                    .setNotification(notification)
                                    .putData("userId", recipient.getId().toString())
                                    .build()

                ).toList();


        if (!messages.isEmpty()) {
            if (com.google.firebase.FirebaseApp.getApps().isEmpty()) {
                return;
            }
            try {
                FirebaseMessaging.getInstance().sendEach(messages);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
