package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatMessage;
import com.javanostra.spring.core.entities.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupChatDAO extends JpaRepository<GroupChat, Long> {

    @Query(value = "SELECT gc.* FROM group_chat gc " +
            "JOIN user_group_chats ugc ON gc.id = ugc.group_chat_id " +
            "WHERE ugc.user_id = :userId", nativeQuery = true)
    List<GroupChat> findAllByUserId(@Param("userId") Long userId);
}
