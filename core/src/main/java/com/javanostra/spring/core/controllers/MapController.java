package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.MapObjectDTO;
import com.javanostra.spring.core.entities.City;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.services.CityService;
import com.javanostra.spring.core.services.EventService;
import com.javanostra.spring.core.services.MapService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maps")
@AllArgsConstructor
public class MapController {

    private final MapService mapService;
    private final EventService eventService;

    @GetMapping("/locations")
    public List<MapObjectDTO> findAllEvents() {
        return mapService.findAllObjects();
    }

}
