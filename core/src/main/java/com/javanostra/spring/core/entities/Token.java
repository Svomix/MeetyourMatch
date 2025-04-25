package com.javanostra.spring.core.entities;

import com.javanostra.spring.core.enums.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.javanostra.spring.core.security.VerificationCodeGenerator.generateCode;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "token_type")
    private TokenType tokenType;


    public Token(String token, User user, LocalDateTime createdAt, LocalDateTime expiredAt, TokenType type) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.tokenType = type;
    }

    public static Token createTokenForUser(User user, TokenType type) {
        LocalDateTime now = LocalDateTime.now();
        return new Token(generateCode(), user, now, now.plusMinutes(15), type);
    }
}
