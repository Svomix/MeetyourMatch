package com.javanostra.spring.core.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "Events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    private String price;

    private Timestamp date;

    @Convert(converter = LocationConverter.class)
    private Location location;

    @Column(name = "cover_img_url")
    private String coverImgUrl;

    @Column(name = "source_url")
    private String sourceUrl;
}

