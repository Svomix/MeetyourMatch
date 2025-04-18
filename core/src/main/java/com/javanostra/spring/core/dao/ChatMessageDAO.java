package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChatMessageDAO extends JpaRepository<ChatMessage, Long>, PagingAndSortingRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatId(String s);
}
