package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.FullUserProfileDTO;
import com.javanostra.spring.core.dto.UserActionDTO;
import com.javanostra.spring.core.entities.Event;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserInterest;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.UserAlreadyExistsException;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    @NonNull
    private final FileService fileService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final CityService cityService;
    @NonNull
    private final ContextRepository contextRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @NonNull
    private final InterestService interestService;
    @NonNull
    private final AuthenticationService authenticationService;
    @NonNull
    private final UserActionsService userActionsService;
    private final EventService eventService;

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    @GetMapping("/getInfo")
    public ResponseEntity<FullUserProfileDTO> getAccountInfo() {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser))
            return ResponseEntity.ok(mapper.convertValue(currentUser, FullUserProfileDTO.class));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/interests")
    public ResponseEntity<Set<UserInterest>> getMyInterests() {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser))
            return ResponseEntity.ok(interestService.getUserInterests(currentUser));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/interests")
    public ResponseEntity<String> addInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.addUserInterest(currentUser, interest);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @DeleteMapping("/interests")
    public ResponseEntity<String> removeInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.removeUserInterest(currentUser, interest);
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/like")
    public ResponseEntity<UserActionDTO> setLiked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if(Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.setLiked(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getIsLiked())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/dislike")
    public ResponseEntity<UserActionDTO> setDisliked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if(Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.setDisliked(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getIsDisliked())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/calendar")
    public ResponseEntity<UserActionDTO> setCalendar(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if(Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.setCalendar(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getInCalendar())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/events/{event_id}/actions")
    public ResponseEntity<UserActionDTO> getEventAction(@PathVariable("event_id") Long eventId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if(Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.getUserEventActions(currentUser, event));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/events/calendar")
    public ResponseEntity<List<UserActionDTO>> getCalendar() throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if(Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.findUserEventsInCalendar(currentUser));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/setCity")
    public ResponseEntity<ResponseDTO> setCity(@RequestParam("city") Long city_id){
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                currentUser.setCity(cityService.findCityById(city_id));
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Город обновлён!"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Нет авторизации!"));
        }catch (NoSuchElementException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "Такого города не существует!"));
        }
    }

    @PostMapping("/setEmail")
    public ResponseDTO setEmail(@NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if(userService.userExistsByEmail(email))
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");

        User user = userService.getCurrentUser();

        user.setEmail(email);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setImage")
    public ResponseEntity<ResponseDTO> setImage(@RequestParam("file") MultipartFile file){
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                String object_id = currentUser.getId().toString();
                fileService.uploadFile("avatars", object_id, file.getInputStream(), file.getContentType());
                currentUser.setAvatarPath(FileService.STATIC_PREFIX + FileService.AVATAR_PREFIX + "/" + object_id);
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Image set"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (NoSuchElementException | IOException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "no image"));
        }
    }

    @PostMapping("/setName")
    public ResponseDTO setUsername(@NonNull @RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if(userService.userExists(username))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        User user = userService.getCurrentUser();

        user.setUsername(username);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setPassword")
    public ResponseDTO setPassword(@NonNull @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response){
        User user = userService.getCurrentUser();

        authenticationService.ChangePassword(user, passwordEncoder.encode(password));

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed password successfully");
    }
}
