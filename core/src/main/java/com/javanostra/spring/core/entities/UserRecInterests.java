package com.javanostra.spring.core.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRecInterests {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private double weight;
    private Long user_id;
    private String interest;
}
