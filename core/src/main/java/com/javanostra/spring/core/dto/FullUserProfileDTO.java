package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.City;
import com.javanostra.spring.core.entities.UserAuthority;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FullUserProfileDTO extends UserProfileDTO {
    private Set<UserAuthority> authorities;
    private String email;
}
