package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.entities.Event;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import com.javanostra.spring.core.services.EventService;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public Page<Event> findAllEvents(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return eventService.findAllEvents(PageRequest.of(offset, limit));
    }

    @GetMapping("/{event_id}")
    public Event findEventById(@PathVariable("event_id") Long eventId) {
        return eventService.findEventById(eventId);
    }

    @GetMapping("/{event_id}/attributes")
    public Page<Attribute> findEventAttributesByEventId(
            @PathVariable("event_id") Long eventId,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return eventService.findEventAttributesByEventId(eventId, PageRequest.of(offset, limit));
    }

    @PostMapping("/{event_id}/attributes/{attr_id}")
    public void createEventAttributeByEventId(
            @PathVariable("event_id") Long eventId,
            @PathVariable("attr_id") Long attrId,
            @RequestBody String value) {
        eventService.createEventAttributeValue(eventId, attrId, value);
    }

    @DeleteMapping("/{event_id}/attributes/{attr_id}")
    public void deleteEventAttributeByAttrId(
            @PathVariable("event_id") Long eventId,
            @PathVariable("attr_id") Long attrId) {
        eventService.deleteEventAttributeByAttrId(eventId, attrId);
    }

    @PostMapping
    public void saveEvent(@RequestBody Event event) {
        eventService.saveEvent(event);
    }

    @PutMapping
    public void updateEvent(@RequestBody Event event) {
        eventService.updateEvent(event);
    }

    @DeleteMapping("/{event_id}")
    public void deleteEvent(@PathVariable("event_id") Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
