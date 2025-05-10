package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.Location;
import com.javanostra.spring.core.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.util.List;

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
    private List<TagDTO> tags;
    private Location location;
    private String coverImgUrl;
    private String sourceUrl;
    private UserProfileDTO createdBy;
    private UserActionEDTO userAction;
    private UserActionCountersDTO userActionCounters;
}
