package com.javanostra.spring.core.services;

import com.javanostra.spring.core.dao.TokenDAO;
import com.javanostra.spring.core.entities.Token;
import com.javanostra.spring.core.enums.TokenType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TokenService {
    private final TokenDAO tokenDAO;

    public void saveToken(Token Token) {
        tokenDAO.save(Token);
    }

    public Token getToken(Long userId, TokenType tokenType) {
        return tokenDAO.findByUserIdAndTokenTypeIs(userId, tokenType);
    }

    public void deleteToken(Token token) {
        tokenDAO.delete(token);
    }

    public void updateToken(Token token) {
        tokenDAO.save(token);
    }
}
