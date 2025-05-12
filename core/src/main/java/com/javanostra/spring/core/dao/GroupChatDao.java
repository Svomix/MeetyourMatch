package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.GroupChat;
import com.javanostra.spring.core.entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GroupChatDao extends JpaRepository<GroupChat, Long>, PagingAndSortingRepository<GroupChat, Long> {

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.chat.id = :chatId")
    List<GroupMessage> findAllByChatId(@Param("chatId") Long chatId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_group_chats WHERE group_chat_id = :chatId AND user_id = :userId", nativeQuery = true)
    int exitFromChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_group_chats (group_chat_id, user_id) VALUES (:chatId, :userId)", nativeQuery = true)
    int addUserToChat(@Param("chatId") Long chatId, @Param("userId") Long userId);
}
