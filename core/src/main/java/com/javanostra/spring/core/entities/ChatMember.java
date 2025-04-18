package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Chats_members")
@IdClass(ChatMemberId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatMessage chat;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String role;
}

