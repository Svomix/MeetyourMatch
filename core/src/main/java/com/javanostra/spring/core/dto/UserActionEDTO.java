package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserActionEDTO {
    private Boolean isLiked = false;
    private Boolean isDisliked = false;
    private Boolean inCalendar = false;
}
