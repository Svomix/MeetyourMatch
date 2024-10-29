package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_liked_events")

public class UserLikedEvents
{
    @Id
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "event_id")
    private Integer eventId;
}
