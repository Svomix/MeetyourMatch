package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.UserEventDTO;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/authorities")
    public List<UserAuthority> getMyAuthorities() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        System.out.println(ctx.getAuthentication());
        return ctx.getAuthentication().getAuthorities().stream().map(a -> (UserAuthority) a).toList();
    }

    @GetMapping
    public Page<User> findAllUsers(
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return userService.findAllUsers(PageRequest.of(offset, limit));
    }

    @GetMapping("/{user_id}")
    public User findUserById(@PathVariable("user_id") Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/{user_id}/events")
    public Page<UserActions> findAllUserEvents(@PathVariable("user_id") Long userId,
                                               @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                               @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return userService.findAllUserEvents(userId, PageRequest.of(offset, limit));
    }

    @GetMapping("/{user_id}/events/calendar")
    public List<UserEventDTO> findUserEventsInCalendar(@PathVariable("user_id") Long userId) {
        return userService.findUserEventsInCalendar(userId);
    }

    @GetMapping("/{user_id}/events/{event_id}")
    public UserEventDTO findUserEventById(@PathVariable("user_id") Long userId,
                                       @PathVariable("event_id") Long eventId) {
        return userService.getUserEventByIds(userId, eventId);
    }

    @GetMapping("/events/{event_id}/liked")
    public Integer getLikes(@PathVariable("event_id") Long eventId) {
        return userService.getLikes(eventId);
    }

    @PutMapping("/{user_id}/events/{event_id}/liked")
    public void setLiked(@PathVariable("user_id") Long userId,
                         @PathVariable("event_id") Long eventId) {
        userService.switchLiked(userId, eventId);
    }

    @PutMapping("/{user_id}/events/{event_id}/disliked")
    public void setDisliked(@PathVariable("user_id") Long userId,
                            @PathVariable("event_id") Long eventId) {
        userService.switchDisliked(userId, eventId);
    }

    @PutMapping("/{user_id}/events/{event_id}/calendar")
    public void setCalendar(@PathVariable("user_id") Long userId,
                            @PathVariable("event_id") Long eventId) {
        userService.switchCalendar(userId, eventId);
    }

//    @GetMapping("/{user_id}/tags")
//    public List<Tag> findUserTags(@PathVariable("user_id") Long userId) {
//        return userService.findUserTagsByUserId(userId);
//    }

//    @GetMapping("/{user_id}/attributes")
//    public Page<Attribute> findEventAttributesByEventId(
//            @PathVariable("user_id") Long userId,
//            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
//            @RequestParam(value = "limit", defaultValue = "5") Integer limit
//    ) {
//        return userService.findUserAttributesByUserId(userId, PageRequest.of(offset, limit));
//    }

    @PostMapping
    public void saveUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PostMapping("/events")
    public void saveUserEvent(@RequestBody UserActions event) {
        userService.saveUserEvent(event);
    }

//    @PostMapping("/{user_id}/attributes/{attr_id}")
//    public void createEventAttributeByEventId(
//            @PathVariable("user_id") Long userId,
//            @PathVariable("attr_id") Long attrId,
//            @RequestBody String value) {
//        userService.createUserAttributeValue(userId, attrId, value);
//    }

//    @DeleteMapping("/{user_id}/attributes/{attr_id}/{value}")
//    public void deleteEventAttributeByEventId(
//            @PathVariable("user_id") Long userId,
//            @PathVariable("attr_id") Long attrId,
//            @PathVariable("value") String value) {
//        userService.deleteUAVByUserAndAttributeAndValue(userId, attrId, value);
//    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PutMapping("/events")
    public void updateUserEvent(@RequestBody UserActions event) {
        userService.updateUserEvent(event);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @DeleteMapping("/events/{event_id}")
    public void deleteUserEventById(@PathVariable("event_id") Long eventId) {
        userService.deleteUserEventById(eventId);
    }

//    @DeleteMapping("/{user_id}/attributes/{attr_id}")
//    public void deleteEventAttributeByAttrId(
//            @PathVariable("user_id") Long userId,
//            @PathVariable("attr_id") Long attrId) {
//        userService.deleteUserAttributeByAttrId(userId, attrId);
//    }
}
