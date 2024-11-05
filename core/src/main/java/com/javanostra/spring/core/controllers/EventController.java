package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Event;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.javanostra.spring.core.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public List<Event> findAllEvents() {
        return eventService.findAllEvents();
    }

    @GetMapping("/{id}")
    public Event findEventById(@PathVariable int id) {
        return eventService.findEventById(id);
    }

    @PostMapping
    public void saveEvent(@RequestBody Event event) {
        eventService.saveEvent(event);
    }

    @PutMapping
    public void updateEvent(@RequestBody Event event) {
        eventService.updateEvent(event);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable int id) {
        eventService.deleteEvent(id);
    }
}
