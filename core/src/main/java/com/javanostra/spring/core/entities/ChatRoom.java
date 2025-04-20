package com.javanostra.spring.core.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private String chatId;
    private String senderId;
    private String recipientId;
}
