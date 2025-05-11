package com.javanostra.spring.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageRequestDTO {
    private Long groupChatId;
    private Long senderId;
    private String content;
}
