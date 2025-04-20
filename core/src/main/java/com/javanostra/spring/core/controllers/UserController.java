package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.EmailVerificationCodeException;
import com.javanostra.spring.core.exceptions.UserDoesNotExistException;
import com.javanostra.spring.core.mail.MailService;
import com.javanostra.spring.core.services.AuthenticationService;
import com.javanostra.spring.core.services.ConfirmationTokenService;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final ConfirmationTokenService confirmationTokenService;
    private final AuthenticationService authenticationService;

    ObjectMapper mapper = new ObjectMapper();
    {
        mapper.registerModule(new Hibernate6Module()); //TODO: move to a bean / class
    }

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
    public ResponseEntity<UserProfileDTO> findUserById(@PathVariable("user_id") Long userId) {
        User user = userService.findUserById(userId);
        if(Objects.isNull(user)){ return ResponseEntity.notFound().build(); }
        return ResponseEntity.ok(mapper.convertValue(user, UserProfileDTO.class));
    }

    @GetMapping("/exists/{email}")
    public Boolean checkUserIfExistsByEmail(@PathVariable("email") String email) {
        return userService.userExistsByEmail(email);
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

    @PostMapping("/sendResetCode")
    public ResponseDTO sendResetPasswordCode(@NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {
        if (!userService.userExistsByEmail(email)) {
            return new ResponseDTO(HttpStatus.BAD_REQUEST.value(), "Пользователя с данной электронной почтой не существует");
        }

        User user = userService.findByEmail(email);

        ConfirmationToken token = ConfirmationToken.createConfirmationTokenForUser(user);
        confirmationTokenService.saveConfirmationToken(token);
        mailService.sendVerificationCodeEmail(user.getEmail(),"Подтвердите вашу электронную почту", token.getToken(), user.getUsername());

        return new ResponseDTO(HttpStatus.OK.value(), "Код был успешно отправлен");
    }

    @GetMapping("/checkResetCode")
    public ResponseDTO checkResetPasswordCode(@NonNull @RequestParam("code") String code, @NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        User user = userService.findByEmail(email);

        if (user != null) {
            ConfirmationToken validToken = confirmationTokenService.getConfirmationToken(user.getId());
            if (LocalDateTime.now().isAfter(validToken.getExpiredAt())) {
                throw new EmailVerificationCodeException("Код подтверждения просрочен");
            }

            if (validToken.getToken().equals(code)) {
                confirmationTokenService.deleteConfirmationToken(validToken);
                return new ResponseDTO(HttpStatus.OK.value(), "Введен правильный код");
            }
            else {
                throw new EmailVerificationCodeException("Введен неправильный код");
            }
        } else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }

    @PutMapping("/updatePassword")
    public ResponseDTO updatePassword(@NonNull @RequestParam("password") String password, @NonNull @RequestParam("email") String email, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findByEmail(email);
        authenticationService.ChangePassword(user, password);
        return new ResponseDTO(HttpStatus.OK.value(), "Пароль обновлен");
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers()
    {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
