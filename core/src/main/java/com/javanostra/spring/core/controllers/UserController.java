package com.javanostra.spring.core.controllers;


import com.javanostra.spring.core.entities.Attribute;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserEvent;
import com.javanostra.spring.core.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

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
    public Page<UserEvent> findAllUserEvents(
            @PathVariable("user_id") Long userId,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return userService.findAllUserEvents(userId, PageRequest.of(offset, limit));
    }

    @GetMapping("/{user_id}/events/{event_id}")
    public UserEvent findUserEventById(@PathVariable("user_id") Long userId, @PathVariable("event_id") Long eventId) {
        return userService.findUserEventById(userId, eventId);
    }

    @GetMapping("/{user_id}/attributes")
    public Page<Attribute> findEventAttributesByEventId(
            @PathVariable("user_id") Long userId,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {
        return userService.findUserAttributesByUserId(userId, PageRequest.of(offset, limit));
    }

    @PostMapping
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @PostMapping("/events")
    public void saveUserEvent(@RequestBody UserEvent event) {
        userService.saveUserEvent(event);
    }

    @PostMapping("/{user_id}/attributes/{attr_id}")
    public void createEventAttributeByEventId(
            @PathVariable("user_id") Long userId,
            @PathVariable("attr_id") Long attrId,
            @RequestBody String value) {
        userService.createUserAttributeValue(userId, attrId, value);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @PutMapping("/events")
    public void updateUserEvent(@RequestBody UserEvent event) {
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

    @DeleteMapping("/{user_id}/attributes/{attr_id}")
    public void deleteEventAttributeByAttrId(
            @PathVariable("user_id") Long userId,
            @PathVariable("attr_id") Long attrId) {
        userService.deleteUserAttributeByAttrId(userId, attrId);
    }
}
