package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatDAO extends JpaRepository<Chat, Integer> {
}
