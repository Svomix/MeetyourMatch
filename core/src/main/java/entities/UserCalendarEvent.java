package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_calendar_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCalendarEvent {
    @EmbeddedId
    private UserCalendarEventId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
