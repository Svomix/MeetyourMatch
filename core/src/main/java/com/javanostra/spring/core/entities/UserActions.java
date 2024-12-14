package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users_event")
@IdClass(UserEventId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActions {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "is_liked", nullable = false)
    private Boolean isLiked = false;

    @Column(name = "is_disliked", nullable = false)
    private Boolean isDisliked = false;

    @Column(name = "in_calendar", nullable = false)
    private Boolean inCalendar = false;
}

