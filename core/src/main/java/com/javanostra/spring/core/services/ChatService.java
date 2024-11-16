package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.ChatDAO;
import com.javanostra.spring.core.entities.Chat;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatDAO chatDAO;

    public List<Chat> findAllChats() {
        return chatDAO.findAll();
    }

    public Chat findChatById(int id) {
        return chatDAO.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public void saveChat(Chat chat) {
        chatDAO.save(chat);
    }

    public void updateChat(Chat chat) {
        chatDAO.save(chat);
    }

    public void deleteChat(int id) {
        chatDAO.deleteById(id);
    }
}
