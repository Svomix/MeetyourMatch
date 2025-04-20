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
        var chatId1 = String.format("%s_%s", senderId, recipientId);
        var chatId2 = String.format("%s_%s", recipientId, senderId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId1)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId2)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomDao.save(senderRecipient);
        chatRoomDao.save(recipientSender);
        return chatId1;
    }

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        String id = chatRoomDao.findBySenderIdAndRecipientId(senderId, recipientId).map(ChatRoom::getChatId).orElse(null);
        if (id == null)
            return createChatId(senderId, recipientId).describeConstable();
        return Optional.of(id);
    }
}