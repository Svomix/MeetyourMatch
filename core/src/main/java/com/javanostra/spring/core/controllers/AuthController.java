package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.ConfirmationTokenDTO;
import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.UpdateConfirmationTokenDTO;
import com.javanostra.spring.core.entities.Tag;
import com.javanostra.spring.core.entities.Token;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.entities.UserRecInterests;
import com.javanostra.spring.core.enums.TokenType;
import com.javanostra.spring.core.exceptions.*;
import com.javanostra.spring.core.mail.MailService;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ContextRepository contextRepository;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final UserRecInterestsService userRecInterestsService;
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ResponseDTO> register(@Valid @ModelAttribute NewUserDTO newUser,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws BaseCoreException {
        if (userService.userExists(newUser.getUsername()))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        if (userService.userExistsByEmail(newUser.getEmail()))
            throw new UserAlreadyExistsException("Пользователь с такой почтой уже существует");

        User newUserEnt = new User();
        newUserEnt.setUsername(newUser.getUsername());
        newUserEnt.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUserEnt.setEmail(newUser.getEmail());
        newUserEnt.setIsEnabled(false);
        newUserEnt.setAuthorities(Set.of(authorizationService.getDefaultGroup()));
        newUserEnt.setCreatedAt(Timestamp.from(Instant.now()));
        userService.createUser(newUserEnt);
        Token token = Token.createTokenForUser(newUserEnt, TokenType.EMAIL_VERIFY);
        tokenService.saveToken(token);
        mailService.sendTokenInformationEmail(newUserEnt.getEmail(), "Подтверждение электронной почты", token, newUserEnt.getUsername());
        //authenticationService.UpdateToken(newUserEnt, request, response);
        for (Tag tag : tagService.findAll()) {
            userRecInterestsService.save(UserRecInterests.builder().user_id(newUserEnt.getId()).interest(tag.getName()).weight(0.1).build());
        }
        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "created account " + newUser.getUsername()));
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO> verifyRegister(@Valid @ModelAttribute ConfirmationTokenDTO token, HttpServletRequest request, HttpServletResponse response) throws BaseCoreException {
        User user = userService.findByEmail(token.getEmail());
        if (user != null) {
            if (user.getIsEnabled()) {
                throw new UserAlreadyEnabledException("Почта пользователя уже подтверждена");
            }

            Token validToken = tokenService.getToken(user.getId(), TokenType.EMAIL_VERIFY);
            if (LocalDateTime.now().isAfter(validToken.getExpiredAt())) {
                throw new EmailVerificationCodeException("Код подтверждения просрочен. Сделайте запрос нового");
            }

            if (validToken.getToken().equals(token.getToken())) {
                user.setIsEnabled(true);
                userService.updateUser(user);
                tokenService.deleteToken(validToken);
                authenticationService.UpdateToken(user, request, response);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Электронная почта подтверждена!"));
            } else {
                throw new EmailVerificationCodeException("Введен неправильный код");
            }
        } else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }

    @PutMapping("/update-code")
    public ResponseEntity<ResponseDTO> updateVerificationCode(@Valid @ModelAttribute UpdateConfirmationTokenDTO tokenDTO) throws BaseCoreException {
        User user = userService.findByEmail(tokenDTO.getEmail());

        if (user != null) {
            if (user.getIsEnabled()) {
                throw new UserAlreadyEnabledException("Почта пользователя уже подтверждена");
            }
            Token token = Token.createTokenForUser(user, TokenType.EMAIL_VERIFY);
            Token previousToken = tokenService.getToken(user.getId(), TokenType.EMAIL_VERIFY);

            if (previousToken == null) {
                throw new TokenStateException("Не поступало попытки зарегистрироваться");
            }

            token.setId(previousToken.getId());
            tokenService.updateToken(token);
            mailService.sendTokenInformationEmail(user.getEmail(), "Подтверждение электронной почты", token, user.getUsername());
            return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Код был выслан на вашу электронную почту"));
        } else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }
}