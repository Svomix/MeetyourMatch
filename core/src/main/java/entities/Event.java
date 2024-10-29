package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer eventId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "date_and_time", nullable = false)
    private Timestamp dateAndTime;

    @Embedded
    @Column(name = "location", nullable = false, columnDefinition = "DECIMAL[2]")
    private Location location;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "source_url")
    private String sourceUrl;

    @ManyToMany
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @ManyToMany(mappedBy = "likedEvents")
    private Set<User> likedByUsers;

    @ManyToMany(mappedBy = "calendarEvents")
    private Set<User> calendarUsers;
}