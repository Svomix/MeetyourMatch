package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ChatRooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String chatId;
    private String senderId;
    private String recipientId;
}
