package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserActionDTO {
    private EventDTO event;

    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Boolean inCalendar = false;
}
