package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_friends")
public class UserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "friend_id")
    private Long friendId;
    @Column(name = "is_accepted")
    private Boolean isAccepted;
}
