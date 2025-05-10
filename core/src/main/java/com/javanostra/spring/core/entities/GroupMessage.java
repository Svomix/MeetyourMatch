package com.javanostra.spring.core.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class GroupMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private GroupChat chat;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String content;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
}
