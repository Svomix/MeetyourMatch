package com.javanostra.spring.core.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Column(length = 10000)
    private String description;

    private String price;

    private Timestamp date;

    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_comments",
            joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "comment_id", referencedColumnName = "id") }
    )
    private List<EventComment> comments;

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_tags",
            joinColumns = { @JoinColumn(name = "event_id", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id", referencedColumnName = "id") }
    )
    private List<Tag> tags;

    @JoinColumn
    @ManyToOne
    private Location location;

    @Column(name = "cover_img_url", length = 512)
    private String coverImgUrl;

    @Column(name = "source_url", length = 512)
    private String sourceUrl;

    @ManyToOne
    @Nullable
    @JoinColumn(name = "creator_id")
    private User createdBy;
}

