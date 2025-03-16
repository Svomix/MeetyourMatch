package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class CommentRequestDTO {
    @NotBlank(message = "null content")
    private String content;
}
