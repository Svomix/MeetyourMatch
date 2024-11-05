package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Chat;
import org.springframework.web.bind.annotation.*;
import com.javanostra.spring.core.services.ChatService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {
    private ChatService chatService;

    @GetMapping
    public List<Chat> findAllChats() {
        return chatService.findAllChats();
    }

    @GetMapping("/{id}")
    public Chat findChatById(@PathVariable int id) {
        return chatService.findChatById(id);
    }

    @PostMapping
    public void saveChat(@RequestBody Chat chat) {
        chatService.saveChat(chat);
    }

    @PutMapping
    public void updateChat(@RequestBody Chat chat) {
        chatService.updateChat(chat);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable int id) {
        chatService.deleteChat(id);
    }
}
