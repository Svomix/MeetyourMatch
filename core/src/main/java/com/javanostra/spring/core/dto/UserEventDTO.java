package com.javanostra.spring.core.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEventDTO {
    private Long user;
    private Long event;

    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Boolean inCalendar = false;
}
