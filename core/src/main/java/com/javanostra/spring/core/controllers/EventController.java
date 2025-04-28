package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.*;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.exceptions.*;
import com.javanostra.spring.core.recommendations.WeightedRandomChoice;
import com.javanostra.spring.core.services.*;
import com.javanostra.spring.core.specifications.EventSearchCriteria;
import com.javanostra.spring.core.specifications.EventSpecification;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.List;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final UserActionsService userActionsService;
    private final AuthorizationService authorizationService;
    private final FileService fileService;
    private final TagService tagService;
    private final LocationService locationService;
    private final UserRecInterestsService userRecInterestsService;
    ObjectMapper mapper = new ObjectMapper();


    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    @GetMapping("/Rec")
    public EventDTO findRec()
    {
        User user = userService.getCurrentUser();
        List<UserRecInterests> interests = userRecInterestsService.findAllById(user.getId());
        UserRecInterests interest = WeightedRandomChoice.weightedChoice(interests);
        Event event = eventService.getRandEvent(interest.getInterest());
        return mapper.convertValue(event, EventDTO.class);
    }

    @GetMapping
    public Page<EventDTO> findAllEvents(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "s", required = false) String search
    ) {
        User user = userService.getCurrentUser();
        EventSpecification specification = new EventSpecification(EventSearchCriteria.fromString(search, tagService));
        Page<Event> events = eventService.findAllEventsRaw(PageRequest.of(page - 1, limit), specification);
        return userActionsService.populateUserActions(events, user);
    }

    @GetMapping("/pageout")
    public List<EventDTO> findAllEventsPageout(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return eventService.findAllEvents(PageRequest.of(offset, limit), null).getContent();
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

    @PostMapping("/uploadEventImage")
    public ResponseEntity<FileUploadedDTO> uploadImage(@RequestParam("file") MultipartFile file) throws BaseCoreException {
        try {
            User currentUser = userService.getCurrentUser();
            if(Objects.nonNull(currentUser)) {
                //if(authorizationService.HasAdminAuthority(currentUser)){
                    String object_id = UUID.randomUUID().toString();
                    fileService.uploadFile("content", object_id, file.getInputStream(), file.getContentType());
                    return ResponseEntity.ok(new FileUploadedDTO(object_id, fileService.getPath("content", object_id)));
                //}
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IOException exception){
            throw new FileUploadFailedException("no image");
        }
    }

    @PostMapping("/uploadEvent")
    public ResponseEntity<EventDTO> uploadEvent(@Valid @RequestBody EventUploadDTO eventDto) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        if(Objects.nonNull(currentUser)) {
            //if (authorizationService.HasAdminAuthority(currentUser)) {
                Event event = new Event();
                event.setTitle(eventDto.getTitle());
                event.setDescription(eventDto.getDescription());
                event.setDate(eventDto.getDate());
                event.setPrice(eventDto.getPrice());
                event.setSourceUrl(eventDto.getSourceUrl());
                if(Objects.nonNull(eventDto.getLocationId())) {
                    try {
                        Long id = Long.parseLong(eventDto.getLocationId());
                        Location loc = locationService.getLocationById(id);
                        event.setLocation(loc);
                    } catch (NumberFormatException e) {
                        throw new FormatErrorException("format id not valid");
                    }
                }
                if(Objects.nonNull(eventDto.getTags())) {
                    List<Tag> tags = new ArrayList<>(List.of());
                    for(Long tag_id : eventDto.getTags()){
                        tags.add(tagService.findById(tag_id));
                    }
                    event.setTags(tags);
                }
                if(Objects.nonNull(eventDto.getCoverFileId())){
                    if(!fileService.fileExists("content", eventDto.getCoverFileId())){
                        throw new FileServiceException("no such image");
                    }
                    event.setCoverImgUrl(fileService.getPath("content", eventDto.getCoverFileId()));
                }
                eventService.saveEvent(event);
                return ResponseEntity.ok(mapper.convertValue(event, EventDTO.class));
            //}
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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
