package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.*;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.EventComment;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.services.UserActionsService;
import com.javanostra.spring.core.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.*;
import com.javanostra.spring.core.services.EventService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final UserActionsService userActionsService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    {
        objectMapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    @GetMapping
    public Page<EventDTO> findAllEvents(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit
    ) {
        User user = userService.getCurrentUser();
        Page<Event> events = eventService.findAllEventsRaw(PageRequest.of(page - 1, limit));
        return userActionsService.populateUserActions(events, user);

    }

    @GetMapping("/pageout")
    public List<EventDTO> findAllEventsPageout(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return eventService.findAllEvents(PageRequest.of(offset, limit)).getContent();
    }

    @GetMapping("/{event_id}")
    public FullEventDTO findEventById(@PathVariable("event_id") Long eventId) throws BaseCoreException {
        User user = userService.getCurrentUser();
        FullEventDTO fullEventDTO = eventService.findEventDtoById(eventId);
        Event event = eventService.findEventById(eventId);
        if(Objects.nonNull(user)) {
            fullEventDTO.setUserAction(userActionsService.getUserEventActionsE(event, user));
        }
        fullEventDTO.setUserActionCounters(userActionsService.getEventCounters(event));
        return fullEventDTO;
    }

    @PostMapping("/{event_id}/comments")
    public CommentDTO addComment(@PathVariable("event_id") Long eventId, @Valid @RequestBody CommentRequestDTO commentDTO) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        EventComment comment = new EventComment();
        comment.setContent(commentDTO.getContent());
        comment.setDate(Timestamp.from(Instant.now()));
        comment.setUser(currentUser);
        return objectMapper.convertValue(eventService.addComment(eventId, comment), CommentDTO.class);
    }

    @DeleteMapping("/{event_id}/comments")
    public EventComment removeComment(@PathVariable("event_id") Long eventId, @RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        EventComment comment = eventService.getComment(id);
        eventService.removeComment(eventId, comment);
        return comment;
    }

//    @GetMapping("/{event_id}/tags")
//    public List<Tag> findEventTags(@PathVariable("event_id") Long eventId) {
//        return eventService.findEventTagsByEventId(eventId);
//    }

//    @GetMapping("/{event_id}/attributes")
//    public Page<Attribute> findEventAttributesByEventId(
//            @PathVariable("event_id") Long eventId,
//            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//            @RequestParam(value = "limit", defaultValue = "30") Integer limit
//    ) {
//        return eventService.findEventAttributesByEventId(eventId, PageRequest.of(offset, limit));
//    }

//    @PostMapping("/{event_id}/attributes/{attr_id}")
//    public void createEventAttributeByEventId(
//            @PathVariable("event_id") Long eventId,
//            @PathVariable("attr_id") Long attrId,
//            @RequestBody String value) {
//        eventService.createEventAttributeValue(eventId, attrId, value);
//    }
//
//    @DeleteMapping("/{event_id}/attributes/{attr_id}")
//    public void deleteEventAttributeByAttrId(
//            @PathVariable("event_id") Long eventId,
//            @PathVariable("attr_id") Long attrId) {
//        eventService.deleteEventAttributeByAttrId(eventId, attrId);
//    }

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
