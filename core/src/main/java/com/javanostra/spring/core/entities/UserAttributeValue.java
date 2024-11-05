package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users_attribute_value")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @Column(nullable = false)
    private String value;
}

