package com.javanostra.spring.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Table(name = "user_authority")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String authority;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities", fetch = FetchType.EAGER)
    private Set<User> users;
}

