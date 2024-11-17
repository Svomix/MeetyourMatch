package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDAO extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {
    Page<Message> findAllMessagesByChatId(Long chatId, Pageable pageable);
    void deleteAllMessagesByChatId(Long chatId);
}
