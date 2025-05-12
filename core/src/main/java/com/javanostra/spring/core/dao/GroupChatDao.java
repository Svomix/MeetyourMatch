package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.GroupChat;
import com.javanostra.spring.core.entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatDao extends JpaRepository<GroupChat, Long>, PagingAndSortingRepository<GroupChat, Long> {

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.chat.id = :chatId")
    List<GroupMessage> findAllByChatId(@Param("chatId") Long chatId);

}
