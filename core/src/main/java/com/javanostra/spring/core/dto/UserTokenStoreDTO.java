package com.javanostra.spring.core.dto;

import com.javanostra.spring.core.entities.UserAuthority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenStoreDTO {

    private Set<UserAuthority> authorities;
    private String email;

}
