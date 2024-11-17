package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Entity
@Table(name = "user_authority")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authority_id;

    @Column(nullable = false)
    private String authority;

    @ManyToMany(cascade = { CascadeType.MERGE }, mappedBy = "authorities", fetch = FetchType.EAGER)
    private Set<User> users;
}

