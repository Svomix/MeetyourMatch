package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "authorities",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "authority_id", referencedColumnName = "id") }
    )
    private Set<UserAuthority> authorities;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 75)
    private String email;

    @Column(nullable = false, length = 80)
    private String password;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(length = 1)
    private String gender;

    @Column(length = 100)
    private String description;

    @Column(name = "avatar_path")
    private String avatarPath;
}

