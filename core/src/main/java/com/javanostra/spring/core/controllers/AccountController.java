package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dao.UserFirebaseTokenDAO;
import com.javanostra.spring.core.dto.*;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.enums.Relation;
import com.javanostra.spring.core.exceptions.*;
import com.javanostra.spring.core.dto.FriendAcceptNotificationDTO;
import com.javanostra.spring.core.dto.FriendRequestNotificationDTO;
import com.javanostra.spring.core.services.NotificationService;
import com.javanostra.spring.core.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
    private final InterestService interestService;
    @NonNull
    private final AuthenticationService authenticationService;
    @NonNull
    private final UserActionsService userActionsService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final UserFirebaseTokenService userFirebaseTokenService;
    @NonNull
    private final UserRecInterestsService userRecInterestsService;
    private final UserFirebaseTokenDAO userFirebaseTokenDAO;
    ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

    @GetMapping("/getInfo")
    public ResponseEntity<FullUserProfileDTO> getAccountInfo() {
        User currentUser = userService.getCurrentUser();

        if (Objects.nonNull(currentUser))
            return ResponseEntity.ok(mapper.convertValue(currentUser, FullUserProfileDTO.class));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/interests")
    public ResponseEntity<Set<UserInterest>> getMyInterests() {
        User currentUser = userService.getCurrentUser();

        if (Objects.nonNull(currentUser))
            return ResponseEntity.ok(interestService.getUserInterests(currentUser));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/interests")
    public ResponseEntity<String> addInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        if (Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.addUserInterest(currentUser, interest);
            UserRecInterests interestRec = userRecInterestsService.findByIdAndName(currentUser.getId(), interest.getName());
            if (interestRec.getWeight() < 0.75) {
                interestRec.setWeight(interestRec.getWeight() + 0.2);
                userRecInterestsService.save(interestRec);
            }
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @DeleteMapping("/interests")
    public ResponseEntity<String> removeInterest(@RequestParam("id") Integer id) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if (Objects.nonNull(currentUser)) {
            UserInterest interest = interestService.getUserInterestById(id);
            interestService.removeUserInterest(currentUser, interest);
            UserRecInterests interestRec = userRecInterestsService.findByIdAndName(currentUser.getId(), interest.getName());
            if (interestRec.getWeight() > 0.2) {
                interestRec.setWeight(interestRec.getWeight() - 0.15);
                userRecInterestsService.save(interestRec);
            }
            return ResponseEntity.ok("success");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/like")
    public ResponseEntity<UserActionDTO> setLiked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if (Objects.nonNull(currentUser)) {
            for (Tag tag : event.getTags()) {
                String name = tag.getName();
                UserRecInterests interestRec = userRecInterestsService.findByIdAndName(currentUser.getId(), name);
                if (interestRec.getWeight() < 0.75) {
                    interestRec.setWeight(interestRec.getWeight() + 0.005);
                    userRecInterestsService.save(interestRec);
                }
            }
            return ResponseEntity.ok(userActionsService.setLiked(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getIsLiked())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/dislike")
    public ResponseEntity<UserActionDTO> setDisliked(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if (Objects.nonNull(currentUser)) {
            for (Tag tag : event.getTags()) {
                String name = tag.getName();
                UserRecInterests interestRec = userRecInterestsService.findByIdAndName(currentUser.getId(), name);
                if (interestRec.getWeight() > 0.1) {
                    interestRec.setWeight(interestRec.getWeight() - 0.005);
                    userRecInterestsService.save(interestRec);
                }
            }
            return ResponseEntity.ok(userActionsService.setDisliked(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getIsDisliked())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/events/{event_id}/calendar")
    public ResponseEntity<UserActionDTO> setCalendar(@PathVariable("event_id") Long eventId, @RequestParam("value") Optional<Boolean> value) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if (Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.setCalendar(currentUser, event, value.orElseGet(() -> !userActionsService.getUserEventActions(currentUser, event).getInCalendar())));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/events/{event_id}/actions")
    public ResponseEntity<UserActionDTO> getEventAction(@PathVariable("event_id") Long eventId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.findEventById(eventId);

        if (Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.getUserEventActions(currentUser, event));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @GetMapping("/events/calendar")
    public ResponseEntity<List<UserActionDTO>> getCalendar() throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if (Objects.nonNull(currentUser)) {
            return ResponseEntity.ok(userActionsService.findUserEventsInCalendar(currentUser));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PostMapping("/setCity")
    public ResponseEntity<ResponseDTO> setCity(@RequestParam("city") Long city_id) {
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                currentUser.setCity(cityService.findCityById(city_id));
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Город обновлён!"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseDTO(HttpStatus.UNAUTHORIZED.value(), "Нет авторизации!"));
        } catch (NoSuchElementException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "Такого города не существует!"));
        }
    }

    @PostMapping("/setEmail")
    public ResponseDTO setEmail(@NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if (userService.userExistsByEmail(email))
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");

        User user = userService.getCurrentUser();

        user.setEmail(email);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setImage")
    public ResponseEntity<ResponseDTO> setImage(@RequestParam("file") MultipartFile file) throws BaseCoreException {
        try {
            User currentUser = userService.getCurrentUser();

            if (Objects.nonNull(currentUser)) {
                String object_id = UUID.randomUUID().toString();
                fileService.uploadFile("avatars", object_id, file.getInputStream(), file.getContentType());
                currentUser.setAvatarPath(fileService.getPath("avatars", object_id));
                userService.updateUser(currentUser);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Image set"));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (IOException exception) {
            throw new FileUploadFailedException("no image");
        }
    }

    @PostMapping("/setName")
    public ResponseDTO setUsername(@NonNull @RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        if (userService.userExists(username))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        User user = userService.getCurrentUser();

        user.setUsername(username);
        userService.updateUser(user);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed username successfully");
    }

    @PostMapping("/setPassword")
    public ResponseDTO setPassword(@NonNull @RequestParam("password") String password, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.getCurrentUser();

        authenticationService.ChangePassword(user, password);

        authenticationService.UpdateToken(user, request, response);

        return new ResponseDTO(HttpStatus.OK.value(), "changed password successfully");
    }

    @GetMapping("/getRelation")
    public RelationUserDTO getUserRelation(@RequestParam("user_id") Long userId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }
        UserProfileDTO userDTO = mapper.convertValue(user, UserProfileDTO.class);
        Relation myRelation = userService.getRelation(currentUser.getId(), userId);
        Relation userRelation = userService.getRelation(userId, currentUser.getId());
        return RelationUserDTO.createFromUserProfileDTO(userDTO, myRelation, userRelation);
    }

    @GetMapping("/friends")
    public Page<UserProfileDTO> getFriends(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "name_pattern", defaultValue = "") String namePattern
    ) {
        User user = userService.getCurrentUser();
        return userService.getFriends(user, PageRequest.of(page - 1, limit), namePattern);
    }

    @GetMapping("/requests/outgoing")
    public Page<UserProfileDTO> getOutgoingFriendRequests(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "name_pattern", defaultValue = "") String namePattern
    ) {
        User user = userService.getCurrentUser();

        return userService.getOutgoingFriendRequests(user.getId(), PageRequest.of(page - 1, limit), namePattern);
    }

    @GetMapping("/requests/ingoing")
    public Page<UserProfileDTO> getIngoingFriendRequests(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "name_pattern", defaultValue = "") String namePattern
    ) {
        User user = userService.getCurrentUser();

        return userService.getIngoingFriendRequests(user.getId(), PageRequest.of(page - 1, limit), namePattern);
    }

    @PostMapping("/requests")
    public ResponseDTO sendFriendRequest(@RequestParam("user_id") Long userId) throws BaseCoreException {
        User user = userService.getCurrentUser();

        if (!userService.userExistsById(userId)) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (user.getId().equals(userId)) {
            throw new UserIsSameException();
        }

        if (userService.checkIfInRequest(userId, user.getId())) {
            throw new FriendRequestStateException("Заявка в друзья отправлена уже с другой стороны");
        }

        if (userService.checkIfInBlocked(userId, user.getId())) {
            throw new UserAreBlockedException("Этот пользователь вас заблокировал");
        }

        if (userService.checkIfInBlocked(user.getId(), userId)) {
            throw new UserAreBlockedException("Этот пользователь заблокирован у вас");
        }

        if (userService.checkIfInFriends(userId, user.getId())) {
            throw new UserAlreadyFriendException();
        }

        userService.sendFriendRequest(user.getId(), userId);

        notificationService.sendNotification(
                new FriendRequestNotificationDTO(user.getUsername()),
                userService.findUserById(userId)
        );

        return new ResponseDTO(HttpStatus.OK.value(), "Пользователю была отправлена заявка в друзья");
    }

    @PutMapping("/requests")
    public ResponseDTO acceptFriendRequest(@RequestParam("user_id") Long userId) throws BaseCoreException {
        User user = userService.getCurrentUser();

        if (!userService.userExistsById(userId)) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (!userService.checkIfInRequest(userId, user.getId())) {
            throw new FriendRequestStateException("Пользователь не подавал заявку в друзья");
        }

        if (userService.checkIfInFriends(userId, user.getId())) {
            throw new UserAlreadyFriendException();
        }

        userService.acceptFriendRequest(user.getId(), userId);

        notificationService.sendNotification(
                new FriendAcceptNotificationDTO(user.getUsername()),
                userService.findUserById(userId)
        );

        return new ResponseDTO(HttpStatus.OK.value(), "Вы приняли заявку в друзья");
    }

    @DeleteMapping("/requests")
    public ResponseDTO deleteFriendRequest(@RequestParam("user_id") Long userId) throws BaseCoreException {
        User user = userService.getCurrentUser();

        if (!userService.userExistsById(userId)) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (userService.checkIfInRequest(user.getId(), userId)) {
            userService.deleteFriendRequest(userId, user.getId());
            return new ResponseDTO(HttpStatus.OK.value(), "Вы отозвали заявку в друзья");
        }

        if (!userService.checkIfInRequest(userId, user.getId())) {
            throw new FriendRequestStateException("Пользователь не подавал заявку в друзья");
        }

        if (userService.checkIfInFriends(userId, user.getId())) {
            throw new UserAlreadyFriendException();
        }

        userService.deleteFriendRequest(user.getId(), userId);
        return new ResponseDTO(HttpStatus.OK.value(), "Вы отклонили заявку в друзья");
    }

    @DeleteMapping("/friends")
    public ResponseDTO deleteFriend(@RequestParam("friend_id") Long friendId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getId().equals(friendId)) {
            throw new UserIsSameException();
        }

        User friend = userService.findUserById(friendId);

        if (friend == null) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (!userService.checkIfInFriends(currentUser.getId(), friend.getId())) {
            throw new UserNotFriendException("Этот пользователь не является вашим другом");
        }

        userService.deleteFriend(currentUser, friend);

        return new ResponseDTO(HttpStatus.OK.value(), "Пользователь был удален из друзей");
    }

    @GetMapping("/blocked")
    public Page<UserProfileDTO> getBlocked(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "name_pattern", defaultValue = "") String namePattern
    ) {
        User user = userService.getCurrentUser();
        return userService.getBlocked(user, PageRequest.of(page - 1, limit), namePattern);
    }

    @PostMapping("/blocked")
    public ResponseDTO addBlocked(@RequestParam("blocked_id") Long blockedId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getId().equals(blockedId)) {
            throw new UserIsSameException();
        }

        User blocked = userService.findUserById(blockedId);

        if (blocked == null) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (userService.checkIfInBlocked(currentUser.getId(), blocked.getId())) {
            throw new UserAreBlockedException("Этот пользователь уже заблокирован");
        }

        userService.addBlocked(currentUser, blocked);

        return new ResponseDTO(HttpStatus.OK.value(), "Пользователь добавлен в черный список");
    }

    @DeleteMapping("/blocked")
    public ResponseDTO deleteBlocked(@RequestParam("blocked_id") Long blockedId) throws BaseCoreException {
        User currentUser = userService.getCurrentUser();

        if (currentUser.getId().equals(blockedId)) {
            throw new UserIsSameException();
        }

        User blocked = userService.findUserById(blockedId);

        if (blocked == null) {
            throw new UserDoesNotExistException("Несуществующий пользователь");
        }

        if (!userService.checkIfInBlocked(currentUser.getId(), blocked.getId())) {
            throw new UserAreNotBlockedException("Этот пользователь не заблокирован");
        }

        userService.deleteBlocked(currentUser, blocked);

        return new ResponseDTO(HttpStatus.OK.value(), "Пользователь убран из черного списка");
    }

    @PutMapping("/firebase-token")
    public ResponseDTO updateFirebaseToken(@RequestBody UserFirebaseTokenDTO userFirebaseTokenDTO) throws BaseCoreException {

        User user = userService.getCurrentUser();
        if (!Objects.equals(user.getId(), userFirebaseTokenDTO.getUserId())) {
            throw new UserIsNotSameException();
        }
        System.out.println(userFirebaseTokenDTO);
        UserFirebaseToken userFirebaseToken = new UserFirebaseToken(
                user,
                userFirebaseTokenDTO.getDeviceId(),
                LocalDateTime.now(),
                userFirebaseTokenDTO.getFirebaseToken()
        );

        userFirebaseTokenService.save(userFirebaseToken);

        return new ResponseDTO(HttpStatus.OK.value(), "Firebase токен успешно обновлен");
    }

    @DeleteMapping("/firebase-token")
    public ResponseDTO deleteFirebaseToken(@RequestBody UserFirebaseTokenDTO userFirebaseTokenDTO) throws BaseCoreException {
        User user = userService.getCurrentUser();

        if (!Objects.equals(user.getId(), userFirebaseTokenDTO.getUserId())) {
            throw new UserIsNotSameException();
        }

        UserFirebaseToken userFirebaseToken = new UserFirebaseToken(
                user,
                userFirebaseTokenDTO.getDeviceId(),
                LocalDateTime.now(),
                userFirebaseTokenDTO.getFirebaseToken()
        );

        userFirebaseTokenService.delete(userFirebaseToken);

        return new ResponseDTO(HttpStatus.OK.value(), "Firebase токен успешно удален");
    }

    @PutMapping("/online-status")
    public ResponseDTO updateOnlineStatus() {
        User user = userService.getCurrentUser();
        user.setLastSeenAt(Timestamp.from(Instant.now()));
        userService.updateUser(user);
        return new ResponseDTO(HttpStatus.OK.value(), "Продлен онлайн статус");
    }
}
