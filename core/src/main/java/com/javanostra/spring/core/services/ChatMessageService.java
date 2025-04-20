package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.ChatMessageDAO;
import com.javanostra.spring.core.entities.ChatMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageDAO messageDAO;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                true).orElseThrow(null); // ecx;
        chatMessage.setChatId(chatId);
        messageDAO.save(chatMessage);
        return chatMessage;
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(messageDAO::findByChatId).orElse(new ArrayList<>());

    }
}
