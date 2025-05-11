package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatInfoDTO {
    private Long id;
    private String username;
    private String avatarPath;
    private Boolean isGroup;
    private String lastMessage;
    private Timestamp lastMessageTime;
}
