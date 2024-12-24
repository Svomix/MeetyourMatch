package com.javanostra.spring.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.javanostra.spring.core.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapObjectDTO {
    private String id;
    private String title;
    private String address;
    private Double latitude;
    private Double longitude;

    private List<Event> events;
}
