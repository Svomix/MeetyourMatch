package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.ChatDAO;
import com.javanostra.spring.core.dao.MessageDAO;
import com.javanostra.spring.core.entities.Chat;
import com.javanostra.spring.core.entities.Message;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatDAO chatDAO;
    private final MessageDAO messageDAO;

    public Page<Chat> findAllChats(Pageable pageable) {
        return chatDAO.findAll(pageable);
    }

    public Page<Message> findAllMessagesByChatId(Long chatId, Pageable pageable) {
        return messageDAO.findAllMessagesByChatId(chatId, pageable);
    }

    public Chat findChatById(Long chatId) {
        return chatDAO.findById(chatId).orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public void saveChat(Chat chat) {
        chatDAO.save(chat);
    }

    @Transactional
    public void updateChat(Chat chat) {
        chatDAO.save(chat);
    }

    @Transactional
    public void deleteChat(Long chatId) {
        chatDAO.deleteById(chatId);
    }

    @Transactional
    public void createMessage(Message message) {
        messageDAO.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        messageDAO.deleteById(messageId);
    }

    @Transactional
    public void deleteAllMessages(Long chatId) {
        messageDAO.deleteAllMessagesByChatId(chatId);
    }
}
