package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.ChatInfoDTO;
import com.javanostra.spring.core.dto.ChatNotificationDTO;
import com.javanostra.spring.core.dto.GroupChatMessageNotificationDTO;
import com.javanostra.spring.core.dto.GroupMessageRequestDTO;
import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.GroupChat;
import com.javanostra.spring.core.entities.GroupMessage;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.dto.NewMessageNotificationDTO;
import com.javanostra.spring.core.services.ChatMessageService;
import com.javanostra.spring.core.services.GroupChatService;
import com.javanostra.spring.core.services.NotificationService;
import com.javanostra.spring.core.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private final NotificationService notificationService;
    private final GroupChatService groupChatService;

    @GetMapping("/messagesHistory/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatHistoryMessages(@PathVariable("recipientId") String recipientId) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(chatMessageService.findChatMessages(user.getUsername(), recipientId));
    }

    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatInfoDTO>> findChatRooms() {
        User user = userService.getCurrentUser();
        if (user == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(userService.getUserChats(user.getUsername()));
    }

    @MessageMapping("/chat")
    public ChatMessage processMessages(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(new Timestamp(new Date().getTime()));
        ChatMessage savedMsg = chatMessageService.save(chatMessage);

        ChatNotificationDTO chatNotification = ChatNotificationDTO.builder()
                .id(savedMsg.getId())
                .senderId(savedMsg.getSenderId())
                .recipientId(savedMsg.getRecipientId())
                .content(savedMsg.getContent())
                .timestamp(savedMsg.getTimestamp())
                .build();

        messagingTemplate.convertAndSendToUser(
                chatNotification.getRecipientId(), "/queue/messages",
                chatNotification
        );

        User recipient = userService.findUserByUsername(savedMsg.getRecipientId());
        notificationService.sendNotification(
                new NewMessageNotificationDTO(savedMsg.getSenderId(), savedMsg.getContent()),
                recipient
        );

        if (!chatNotification.getSenderId().equals(chatNotification.getRecipientId())) {
            messagingTemplate.convertAndSendToUser(
                    chatNotification.getSenderId(), "/queue/messages",
                    chatNotification
            );
        }
        return savedMsg;
    }

    @PostMapping("/groupChatCreate")
    public ResponseEntity<Long> createGroupChat(@RequestParam List<Long> memberIds, @RequestParam String groupName, @RequestParam(required = false) String avatar) {
        return ResponseEntity.ok(groupChatService.createGroupChat(memberIds, groupName, avatar));
    }

    @MessageMapping("/groupChat")
    public void processGroupChatMessage(@Payload GroupMessageRequestDTO messageRequest) {
        GroupMessage savedMessage = groupChatService.saveGroupMessage(
                messageRequest.getGroupChatId(),
                messageRequest.getSenderId(),
                messageRequest.getContent()
        );

        GroupChatMessageNotificationDTO notification = GroupChatMessageNotificationDTO.builder()
                .id(savedMessage.getId())
                .groupChatId(savedMessage.getChat().getId())
                .senderId(savedMessage.getUser().getId())
                .senderUsername(savedMessage.getUser().getUsername())
                .senderAvatarPath(savedMessage.getUser().getAvatarPath())
                .content(savedMessage.getContent())
                .timestamp(savedMessage.getTimestamp())
                .build();

        String destination = "/topic/group/" + messageRequest.getGroupChatId();
        messagingTemplate.convertAndSend(destination, notification);

        messagingTemplate.convertAndSendToUser(
                savedMessage.getUser().getUsername(),
                "/queue/groupMessageReceipt",
                "Message sent to group " + messageRequest.getGroupChatId()
        );
    }

    @GetMapping("/groupChatHistory")
    public ResponseEntity<List<GroupMessage>> findGroupChatHistory(@RequestParam Long groupChatId) {
        List<GroupMessage> middle = groupChatService.getGroupMessages(groupChatId);
        return ResponseEntity.ok(middle);
    }

    @GetMapping("/groupExit")
    public ResponseEntity<Long> exitGroupChat(@RequestParam Long groupChatId) {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(groupChatService.exitGroupChat(groupChatId, user));
    }

    @PostMapping("/groupAddMembers")
    public ResponseEntity<Long> addMembersToGroupChat(@RequestParam Long groupChatId, @RequestBody List<Long> memberIdsToAdd) {
        return ResponseEntity.ok(groupChatService.addMembersToGroup(groupChatId, memberIdsToAdd));
    }
}