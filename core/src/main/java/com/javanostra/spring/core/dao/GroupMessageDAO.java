package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageDAO extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByChatIdOrderByTimestampAsc(Long chatId);

    List<GroupMessage> findByChatIdOrderByTimestampDesc(Long chatId);
}
