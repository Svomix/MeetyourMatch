package com.javanostra.spring.core.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.javanostra.spring.core.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
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

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authorities",
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

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_interests",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "interest_id", referencedColumnName = "id") }
    )
    private Set<UserInterest> interests;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "status")
    private Status status;
}

