package com.javanostra.spring.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javanostra.spring.core.dao.EventDAO;
import com.javanostra.spring.core.dao.LocationDAO;
import com.javanostra.spring.core.dto.MapObjectDTO;
import com.javanostra.spring.core.entities.Location;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {
    @NonNull
    private final EventDAO eventDAO;
    @NonNull
    private final LocationDAO locationDAO;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MapObjectDTO> findAllObjects() {
        List<Location> locations = locationDAO.findAll();
        List<MapObjectDTO> objects = new ArrayList<>();
        for (Location location : locations) {
            MapObjectDTO mapObjectDTO = objectMapper.convertValue(location, MapObjectDTO.class);
            mapObjectDTO.setEvents(eventDAO.findEventsByLocation(location));
            objects.add(mapObjectDTO);
        }
        return objects;
    }
}
