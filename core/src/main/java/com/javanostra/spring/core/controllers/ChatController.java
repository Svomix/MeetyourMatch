package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Chat;
import com.javanostra.spring.core.entities.Message;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.javanostra.spring.core.services.ChatService;


@RestController
@RequestMapping("/api/v1/chats")
@AllArgsConstructor
public class ChatController {
    private ChatService chatService;

    @GetMapping
    public Page<Chat> findAllChats(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return chatService.findAllChats(PageRequest.of(offset, limit));
    }

    @GetMapping("/{chat_id}/messages")
    public Page<Message> findAllMessagesByChatId(
            @PathVariable("chat_id") Long chatId,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return chatService.findAllMessagesByChatId(chatId, PageRequest.of(offset, limit));
    }

    @PostMapping("/messages")
    public void createMessage(@RequestBody Message message) {
        chatService.createMessage(message);
    }

    @DeleteMapping("/messages/{message_id}")
    public void deleteMessage(@PathVariable("message_id") Long messageId) {
        chatService.deleteMessage(messageId);
    }

    @DeleteMapping("/{chat_id}/messages")
    public void deleteAllMessages(@PathVariable("chat_id") Long chatId) {
        chatService.deleteAllMessages(chatId);
    }

    @GetMapping("/{chat_id}")
    public Chat findChatById(@PathVariable("chat_id") Long chatId) {
        return chatService.findChatById(chatId);
    }

    @PostMapping
    public void saveChat(@RequestBody Chat chat) {
        chatService.saveChat(chat);
    }

    @PutMapping
    public void updateChat(@RequestBody Chat chat) {
        chatService.updateChat(chat);
    }

    @DeleteMapping("/{chat_id}")
    public void deleteChat(@PathVariable("chat_id") Long chatId) {
        chatService.deleteChat(chatId);
    }
}
