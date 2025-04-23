package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.ChatNotificationDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.services.ChatMessageService;
import com.javanostra.spring.core.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/messages/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("recipientId") String recipientId) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(chatMessageService.findChatMessages(user.getUsername(), recipientId));
    }

    @GetMapping("/chatRooms")
    public ResponseEntity<List<UserProfileDTO>> findChatMessages() {
        User user = userService.getCurrentUser();
        if (user == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(userService.getUserChats(user.getUsername()));
    }

    @MessageMapping("/chat")
    public ChatMessage processMessages(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(new Timestamp(new Date().getTime()));
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
                ChatNotificationDTO.builder()
                        .id(savedMsg.getId())
                        .senderId(savedMsg.getSenderId())
                        .recipientId(savedMsg.getRecipientId())
                        .content(savedMsg.getContent())
                        .timestamp(savedMsg.getTimestamp())
                        .build()
        );
        return savedMsg;
    }
}