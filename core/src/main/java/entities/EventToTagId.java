package entities;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class EventToTagId implements Serializable
{
    @Serial
    private static final long serialVersionUID = -3760539068793097964L;
    @Column(name = "tag_id")
    private Integer tag;
    @Column(name = "event_id")
    private Integer event;
}
