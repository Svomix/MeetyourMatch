package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateConfirmationTokenDTO {
    @NotBlank(message = "null email")
    @Email
    private String email;
}
