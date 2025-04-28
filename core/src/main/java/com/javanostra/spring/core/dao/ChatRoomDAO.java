package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomDAO extends JpaRepository<ChatRoom, Long>, PagingAndSortingRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.chatId = CONCAT(:senderId, '_', :recipientId)")
    Optional<ChatRoom> findBySenderIdAndRecipientId(@Param("senderId") String senderId,
                                                    @Param("recipientId") String recipientId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.senderId = :id OR cr.recipientId = :id")
    List<ChatRoom> findById(@Param("id") String id);
}

