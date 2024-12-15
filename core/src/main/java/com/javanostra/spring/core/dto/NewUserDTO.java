package com.javanostra.spring.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Data
public class NewUserDTO {
    @NotBlank(message = "null username")
    private String username;
    @NotBlank(message = "null email")
    @Email
    private String email;
    @NotBlank(message = "null password")
    private String password;
}
