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
    ChatRoomDAO chatRoomDao;

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomDao.save(senderRecipient);
        chatRoomDao.save(recipientSender);
        return chatId;
    }

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        return chatRoomDao.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getId)
                .or(() -> {
                    if (createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }
}
