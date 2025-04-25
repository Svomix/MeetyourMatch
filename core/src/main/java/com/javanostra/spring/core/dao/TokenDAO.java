package com.javanostra.spring.core.dao;

import com.javanostra.spring.core.entities.Token;
import com.javanostra.spring.core.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenDAO extends JpaRepository<Token, Long> {
    Token findByUserIdAndTokenTypeIs(Long userId, TokenType tokenType);

    void deleteAllByUserId(Long userId);
}
