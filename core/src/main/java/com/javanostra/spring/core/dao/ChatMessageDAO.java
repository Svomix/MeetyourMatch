package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageDAO extends JpaRepository<ChatMessage, Long>, PagingAndSortingRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String s);
}
