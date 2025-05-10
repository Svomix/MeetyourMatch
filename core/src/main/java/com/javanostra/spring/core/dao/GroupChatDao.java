package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.GroupChat;
import com.javanostra.spring.core.entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupChatDao extends JpaRepository<GroupChat, Long>, PagingAndSortingRepository<GroupChat, Long> {
    @Query("SELECT cm FROM GroupMessage cm WHERE cm.id = :chatId")
    List<GroupMessage> findAllByChatId(@Param("chatId") Long chatId);

}
