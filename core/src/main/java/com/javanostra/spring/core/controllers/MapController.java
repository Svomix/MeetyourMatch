package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.MapObjectDTO;
import com.javanostra.spring.core.dto.NewMapObjectDTO;
import com.javanostra.spring.core.entities.City;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.services.CityService;
import com.javanostra.spring.core.services.EventService;
import com.javanostra.spring.core.services.MapService;
import com.javanostra.spring.core.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/maps")
@AllArgsConstructor
public class MapController {

    private final MapService mapService;
    private final UserService userService;

    @GetMapping("/locations")
    public List<MapObjectDTO> findAllEvents() {
        return mapService.findAllObjects();
    }

    @PostMapping("/locations")
    public ResponseEntity<MapObjectDTO> createMapObject(@RequestBody @Valid NewMapObjectDTO mapObjectDTO) {
        if(Objects.isNull(userService.getCurrentUser())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return ResponseEntity.ok(mapService.createObject(mapObjectDTO));
    }

    @DeleteMapping("/locations/{location_id}")
    public ResponseEntity<String> deleteMapObject(@PathVariable("location_id") Long locationId) throws BaseCoreException {
        if(Objects.isNull(userService.getCurrentUser())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        mapService.deleteLocationById(locationId);
        return ResponseEntity.ok(null);
    }

}
