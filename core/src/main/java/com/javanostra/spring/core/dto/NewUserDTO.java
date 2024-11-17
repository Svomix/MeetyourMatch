package com.javanostra.spring.core.dto;

import lombok.*;

@AllArgsConstructor
@Setter
@Getter
public class NewUserDTO {
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
}
