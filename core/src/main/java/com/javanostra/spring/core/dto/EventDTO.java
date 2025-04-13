package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String price;
    private Timestamp date;
    //private List<CommentDTO> comments;
    private Location location;
    private String coverImgUrl;
    private String sourceUrl;
    private UserActionEDTO userAction;
    private UserActionCountersDTO userActionCounters;
}
