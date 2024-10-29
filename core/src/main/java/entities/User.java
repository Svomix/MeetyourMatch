package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "user_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    @ManyToOne
    @JoinColumn(name = "city", nullable = false)
    private City city;

    @Column(name = "age")
    private Integer age;

    @Column(name = "sex", length = 1)
    private String sex;

    @Column(name = "password", nullable = false, length = 50)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_interests",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> interests;

    @ManyToMany
    @JoinTable(
            name = "user_liked_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> likedEvents;

    @ManyToMany
    @JoinTable(
            name = "user_calendar_events",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> calendarEvents;
}
