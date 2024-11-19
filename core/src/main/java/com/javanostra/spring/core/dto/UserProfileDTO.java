package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.City;
import com.javanostra.spring.core.entities.UserAuthority;
import lombok.*;

import java.util.Set;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDTO {
    private Long id;
    private Set<UserAuthority> authorities;
    private String username;
    private String email;
    private City city;
    private String gender;
    private String description;
    private String avatarPath;
}
