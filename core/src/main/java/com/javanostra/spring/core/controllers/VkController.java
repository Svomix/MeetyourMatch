package com.javanostra.spring.core.controllers;

import com.javanostra.spring.core.dto.NewUserDTO;
import com.javanostra.spring.core.dto.ResponseDTO;
import com.javanostra.spring.core.entities.User;
import com.javanostra.spring.core.exceptions.BaseCoreException;
import com.javanostra.spring.core.exceptions.UserAlreadyExistsException;
import com.javanostra.spring.core.services.AuthenticationService;
import com.javanostra.spring.core.services.AuthorizationService;
import com.javanostra.spring.core.services.UserService;
import com.javanostra.spring.core.vk.VkClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

@RestController
@RequestMapping("/api/vkid")
@AllArgsConstructor
public class VkController {

    private final UserService userService;
    private final VkClient vkClient;
    private final AuthorizationService authorizationService;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> vkRegister(String accessToken,
                                                  @Valid @ModelAttribute NewUserDTO newUser,
                                                  HttpServletRequest request,
                                                  HttpServletResponse response) throws BaseCoreException {
        if (userService.userExists(newUser.getUsername()))
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует");

        vkClient.checkAccessToken(accessToken, newUser.getEmail());
        User newUserEnt = new User();
        newUserEnt.setUsername(newUser.getUsername());
        newUserEnt.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUserEnt.setEmail(newUser.getEmail());
        newUserEnt.setIsEnabled(true);
        newUserEnt.setAuthorities(Set.of(authorizationService.getDefaultGroup()));
        newUserEnt.setCreatedAt(Timestamp.from(Instant.now()));
        userService.createUser(newUserEnt);

        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Успешно создан аккаунт через ВК с никнеймом: " + newUser.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> vkLogin(String accessToken,
                                               String email,
                                               HttpServletRequest request,
                                               HttpServletResponse response) throws BaseCoreException {
        vkClient.checkAccessToken(accessToken, email);
        User vkUser = userService.findByEmail(email);
        authenticationService.UpdateToken(vkUser, request, response);
        return ResponseEntity.ok(new ResponseDTO(HttpStatus.OK.value(), "Успешный логин через ВК"));
    }
}
