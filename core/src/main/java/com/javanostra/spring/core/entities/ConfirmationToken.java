package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.javanostra.spring.core.security.VerificationCodeGenerator.generateCode;

@Entity
@Table(name = "account_verification_token")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;


    public ConfirmationToken(String token, User user, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public static ConfirmationToken createConfirmationTokenForUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        return new ConfirmationToken(generateCode(), user, now, now.plusMinutes(15));
    }
}
