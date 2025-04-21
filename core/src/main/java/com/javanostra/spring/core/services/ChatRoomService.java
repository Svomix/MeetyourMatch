package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.ChatRoomDAO;
import com.javanostra.spring.core.entities.ChatRoom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class ChatRoomService {
    final ChatRoomDAO chatRoomDao;

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        chatRoomDao.save(senderRecipient);
        return chatId;
    }

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        String id = chatRoomDao.findBySenderIdAndRecipientId(senderId, recipientId).map(ChatRoom::getChatId).orElse(null);
        if (id == null)
            return createChatId(senderId, recipientId).describeConstable();
        return Optional.of(id);
    }
}