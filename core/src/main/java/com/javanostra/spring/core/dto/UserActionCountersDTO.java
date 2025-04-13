package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserActionCountersDTO {
    private Long likedCounter;
    private Long dislikedCounter;
    private Long calendarCounter;
}
