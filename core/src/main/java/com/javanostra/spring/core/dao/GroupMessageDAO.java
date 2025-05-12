package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageDAO extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByChatIdOrderByTimestampAsc(Long chatId);

    List<GroupMessage> findByChatIdOrderByTimestampDesc(Long chatId);
}
