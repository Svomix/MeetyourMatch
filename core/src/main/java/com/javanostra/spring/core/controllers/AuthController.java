package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.ConfirmationTokenDTO;
import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.dto.UpdateConfirmationTokenDTO;
import com.javanostra.spring.core.entities.ConfirmationToken;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.exceptions.*;
import com.javanostra.spring.core.mail.MailService;
import com.javanostra.spring.core.security.ContextRepository;
import com.javanostra.spring.core.security.SecurityConfig;
import com.javanostra.spring.core.services.AuthenticationService;
import com.javanostra.spring.core.services.AuthorizationService;
import com.javanostra.spring.core.services.ConfirmationTokenService;
import com.javanostra.spring.core.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import static com.javanostra.spring.core.security.VerificationCodeGenerator.generateCode;

@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthorizationService authorizationService;
    private final ContextRepository contextRepository;
    private final AuthenticationService authenticationService;
    private final ConfirmationTokenService confirmationTokenService;
    private final MailService mailService;

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
        newUserEnt.setCreatedAt(LocalDateTime.now());
        userService.createUser(newUserEnt);

        ConfirmationToken token = ConfirmationToken.createConfirmationTokenForUser(newUserEnt);
        confirmationTokenService.saveConfirmationToken(token);
        mailService.sendVerificationCodeEmail(newUserEnt.getEmail(),"Подтвердите вашу электронную почту", token.getToken(), newUserEnt.getUsername());
        authenticationService.UpdateToken(newUserEnt, request, response);

        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "created account " + newUser.getUsername()));
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO> verifyRegister(@Valid @ModelAttribute ConfirmationTokenDTO token) throws BaseCoreException {
        User user = userService.findByEmail(token.getEmail());
        if (user != null) {
            if (user.getIsEnabled()) {
                throw new UserAlreadyEnabledException("Почта пользователя уже подтверждена");
            }

            ConfirmationToken validToken = confirmationTokenService.getConfirmationToken(user.getId());
            if (LocalDateTime.now().isAfter(validToken.getExpiredAt())) {
                throw new EmailVerificationCodeException("Код подтверждения просрочен");
            }

            if (validToken.getToken().equals(token.getToken())) {
                user.setIsEnabled(true);
                userService.updateUser(user);
                confirmationTokenService.deleteConfirmationToken(validToken);
                return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Электронная почта подтверждена!"));
            }
            else {
                throw new EmailVerificationCodeException("Введен неправильный код");
            }
        }
        else {
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
            ConfirmationToken token = ConfirmationToken.createConfirmationTokenForUser(user);
            token.setId(confirmationTokenService.getConfirmationToken(user.getId()).getId());
            confirmationTokenService.updateConfirmationToken(token);
            mailService.sendVerificationCodeEmail(user.getEmail(), "Подтвердите вашу электронную почту", token.getToken(), user.getUsername());
            return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Код был выслан на вашу электронную почту"));
        }
        else {
            throw new UserDoesNotExistException("Пользователя с данной электронной почтой не существует");
        }
    }
}