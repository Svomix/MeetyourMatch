package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ChatRoomDAO extends JpaRepository<ChatRoom, Long>, PagingAndSortingRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
