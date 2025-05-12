package com.javanostra.spring.core.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.javanostra.spring.core.dto.RelationUserDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.UserProfileDTO;
import com.javanostra.spring.core.entities.*;
import com.javanostra.spring.core.enums.TokenType;
import com.javanostra.spring.core.exceptions.*;
import com.javanostra.spring.core.mail.MailService;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.services.AuthenticationService;
import com.javanostra.spring.core.services.TokenService;
import com.javanostra.spring.core.services.UserService;
import com.javanostra.spring.core.specifications.UserSearchCriteria;
import com.javanostra.spring.core.specifications.UserSpecification;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private final ContextRepository contextRepository;

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
    public Page<UserProfileDTO> findAllUsers(
            @RequestParam(value = "offset", defaultValue = "1") @Min(1) Integer offset,
            @RequestParam(value = "limit", defaultValue = "30") @Min(1) Integer limit,
            @RequestParam(value = "name_pattern", required = false) String namePattern
    ) {
        UserSpecification specification = new UserSpecification(new UserSearchCriteria(namePattern));
        Page<UserProfileDTO> users = userService.findAllUsers(PageRequest.of(offset - 1, limit), specification);
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            return users.map(
                    user -> RelationUserDTO.createFromUserProfileDTO(
                            user,
                            userService.getRelation(currentUser.getId(), user.getId()),
                            userService.getRelation(user.getId(), currentUser.getId())
                            )
            ); //TODO: remove current user from page
        }
        return users;
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

    @GetMapping("/{user_id}/online_status")
    public Timestamp getLastSeenForUser(@PathVariable("user_id") Long userId) {
        User user = userService.findUserById(userId);
        return user.getLastSeenAt();
    }

    @PostMapping
    public void saveUser(@RequestBody User user) {
        userService.createUser(user);
    }

    @PostMapping("/events")
    public void saveUserEvent(@RequestBody UserActions event) {
        userService.saveUserEvent(event);
    }

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

    @PostMapping("/sendResetCode")
    public ResponseDTO sendResetPasswordCode(@NonNull @RequestParam("email") String email) throws BaseCoreException {
        if (!userService.userExistsByEmail(email)) {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }

        User user = userService.findByEmail(email);

        if (tokenService.getToken(user.getId(), TokenType.PASSWORD_RESET) != null) {
            throw new TokenStateException("Токен для обновления пароля уже существует");
        }

        Token token = Token.createTokenForUser(user, TokenType.PASSWORD_RESET);
        tokenService.saveToken(token);
        mailService.sendTokenInformationEmail(user.getEmail(),"Сброс пароля", token, user.getUsername());

        return new ResponseDTO(HttpStatus.OK.value(), "Код был успешно отправлен");
    }

    @PutMapping("/updatePassword")
    public ResponseDTO checkResetPasswordCode(@NonNull @RequestParam("password") String password,
                                              @NonNull @RequestParam("code") String code,
                                              @NonNull @RequestParam("email") String email) throws BaseCoreException {
        User user = userService.findByEmail(email);

        if (user != null) {
            Token validToken = tokenService.getToken(user.getId(), TokenType.PASSWORD_RESET);

            if (validToken == null) {
                throw new TokenStateException("Не создан токен для обновления пароля");
            }

            if (LocalDateTime.now().isAfter(validToken.getExpiredAt())) {
                throw new EmailVerificationCodeException("Код подтверждения просрочен. Сделайте запрос нового");
            }

            if (validToken.getToken().equals(code)) {
                tokenService.deleteToken(validToken);
                authenticationService.ChangePassword(user, password);
                return new ResponseDTO(HttpStatus.OK.value(), "Введен правильный код");
            }
            else {
                throw new EmailVerificationCodeException("Введен неправильный код");
            }
        } else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }

    @PutMapping("/updateCode")
    public ResponseDTO updateResetCode(@NonNull @RequestParam("email") String email) throws BaseCoreException {
        User user = userService.findByEmail(email);

        if (user != null) {
            Token token = Token.createTokenForUser(user, TokenType.PASSWORD_RESET);
            Token previousToken = tokenService.getToken(user.getId(), TokenType.PASSWORD_RESET);

            if (previousToken == null) {
                throw new TokenStateException("Не поступало попытки зарегистрироваться");
            }

            token.setId(previousToken.getId());
            tokenService.updateToken(token);
            mailService.sendTokenInformationEmail(user.getEmail(), "Сброс пароля", token, user.getUsername());
            return new ResponseDTO(HttpStatus.OK.value(), "Код был выслан на вашу электронную почту");
        }
        else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }
}
